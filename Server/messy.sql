start transaction;

create extension if not exists pgcrypto;

create type conversation_type as enum (
    'DIRECT',
    'GROUP'
);

create type device_type as enum (
    'DESKTOP',
    'MOBILE',
    'WEB',
    'OTHER'
);

create type message_type as enum (
    'TEXT',
    'VOICE',
    'SYSTEM'
);

create table clients (
    id uuid not null unique default gen_random_uuid(),
    internal_id uuid primary key default uuidv7(),

    icon_internal_id uuid,
    encrypted_first_name bytea not null,
    encrypted_last_name bytea not null,

    email varchar(254) not null,
    phone varchar(20),

    login_hash bytea not null,
    password_salt bytea not null,

    public_key bytea not null,

    encrypted_private_key bytea not null,
    private_key_iv bytea not null,

    encrypted_data_key bytea not null,
    data_key_iv bytea not null,

    created_at timestamptz not null default now(),
    deleted_at timestamptz,
    last_seen_at timestamptz,

    version bigint not null,

    constraint chk_clients_email_format check (email ~ '^\w+([._%+-]\w+)*\@\w+([.-]\w+)*\.\w+$'),
    constraint chk_clients_phone_format check (phone ~ '\+[0-9]{1,4}[- ][0-9]+$')
);

create unique index ux_clients_email_lower on clients(lower(email));
create unique index ux_clients_phone on clients(phone) nulls distinct;

create table client_sessions (
    client_internal_id uuid not null references clients(internal_id) on delete cascade,
    token_hash bytea not null unique,

    encrypted_private_key_fragment bytea not null,

    device_internal_id uuid,

    created_at timestamptz not null default now(),
    expires_at timestamptz not null,

    last_used_at timestamptz,
    revoked_at timestamptz,

    version bigint not null,

    primary key (client_internal_id, token_hash)
);

create index ix_client_sessions_expires_at on client_sessions(expires_at);

create table client_relations (
    client_internal_id uuid not null references clients(internal_id) on delete cascade,
    target_internal_id uuid not null references clients(internal_id) on delete cascade,

    muted_until timestamptz,

    is_blocked bool not null default false,

    version bigint not null,

    primary key (client_internal_id, target_internal_id)
);

create table devices (
    id uuid not null unique default gen_random_uuid(),

    internal_id uuid not null unique default uuidv7(),
    mac_address macaddr8 not null unique,

    name varchar(255),
    os varchar(255),

    type device_type not null default 'OTHER',

    primary key (internal_id, mac_address)
);

alter table client_sessions add constraint fk_client_sessions_device_internal_id
    foreign key (device_internal_id)
        references devices(internal_id)
        on delete set null;

create table conversations (
    id uuid not null unique default gen_random_uuid(),
    internal_id uuid primary key default uuidv7(),

    type conversation_type not null,

    created_at timestamptz not null default now(),

    last_message_index integer not null default 0,

    version bigint not null
);

create table conversation_participants (
    conversation_internal_id uuid not null references conversations(internal_id) on delete cascade,
    client_internal_id uuid not null references clients(internal_id) on delete cascade,

    nickname varchar(255),
    muted_until timestamptz,
    read_only bool not null default false,
    admin bool not null default false,

    joined_at timestamptz not null default now(),
    left_at timestamptz,

    version bigint not null,

    primary key (conversation_internal_id, client_internal_id)
);

create table direct_conversations (
    conversation_internal_id uuid primary key references conversations(internal_id) on delete cascade,

    client_a_internal_id uuid not null references clients(internal_id) on delete cascade,
    client_b_internal_id uuid not null references clients(internal_id) on delete cascade,

    constraint chk_direct_conversations_clients_not_same check (client_a_internal_id <> client_b_internal_id),

    constraint fk_direct_conversations_client_a_is_participant
        foreign key (conversation_internal_id, client_a_internal_id)
            references conversation_participants(conversation_internal_id, client_internal_id)
            on delete restrict,

    constraint fk_direct_conversations_client_b_is_participant
        foreign key (conversation_internal_id, client_b_internal_id)
            references conversation_participants(conversation_internal_id, client_internal_id)
            on delete restrict
);

create unique index ux_direct_conversations_unordered_pair on direct_conversations (
    least(client_a_internal_id, client_b_internal_id),
    greatest(client_a_internal_id, client_b_internal_id)
);

create table group_conversations (
    conversation_internal_id uuid primary key references conversations(internal_id) on delete cascade,

    name varchar(100) not null,
    icon_internal_id uuid,
    description text,

    created_by_internal uuid references clients(internal_id) on delete set null,
    owner_internal_id uuid references clients(internal_id) on delete set null,

    version bigint not null
);

create table group_conversation_secrets (
    conversation_internal_id uuid not null references conversations(internal_id) on delete cascade,
    client_internal_id uuid not null references clients(internal_id) on delete cascade,

    encrypted_key bytea not null,
    key_nonce bytea not null,

    primary key (conversation_internal_id, client_internal_id)
);

create table files (
    id uuid not null unique default gen_random_uuid(),
    internal_id uuid primary key default uuidv7(),

    uploaded_by_internal uuid references clients(internal_id) on delete set null,

    original_name varchar(255) not null,
    mime_type varchar(255) not null,

    size_bytes bigint not null,
    sha256 bytea not null,

    created_at timestamptz not null default now()
);

create unique index ux_files_size_bytes_sha256 on files(size_bytes, sha256);

alter table clients add constraint fk_clients_icon_internal_id
    foreign key (icon_internal_id)
        references files(internal_id)
        on delete set null;

alter table group_conversations add constraint fk_group_conversations_icon_internal_id
    foreign key (icon_internal_id)
        references files(internal_id)
        on delete set null;

create table file_chunks (
    file_internal_id uuid not null references files(internal_id) on delete cascade,
    chunk_index integer not null,

    encrypted_chunk bytea not null,
    chunk_nonce bytea not null,

    primary key (file_internal_id, chunk_index)
);

create table messages (
    id uuid not null unique default gen_random_uuid(),
    internal_id uuid primary key default uuidv7(),

    message_index integer not null,

    conversation_internal_id uuid not null references conversations(internal_id) on delete cascade,
    sender_internal_id uuid not null references clients(internal_id) on delete restrict,

    message_type message_type not null,
    is_silent bool not null default false,

    encrypted_content bytea,
    content_iv bytea,

    sent_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz,

    version bigint not null,

    constraint fk_messages_sender_is_participant
        foreign key (conversation_internal_id, sender_internal_id)
        references conversation_participants(conversation_internal_id, client_internal_id)
        on delete restrict,

    constraint chk_messages_content_null check ((encrypted_content is null) = (content_iv is null))
);

create unique index ux_messages_conversation_message_index on messages(conversation_internal_id, message_index desc);

create index ix_messages_sender on messages(sender_internal_id);
create index ix_messages_conversation_updated_at on messages(conversation_internal_id, updated_at);

create or replace function assign_message_index() returns trigger language plpgsql
as $$
    begin
        update conversations set last_message_index = last_message_index + 1
        where id = new.conversation_internal_id returning last_message_index into new.message_index;

        if new.message_index is null then
            raise exception 'Conversation with internalId % does not exist', new.conversation_internal_id;
        end if;

        return new;
    end;
$$;

create trigger trg_assign_message_index before insert on messages
    for each row
    execute function assign_message_index();

create table message_attachments (
    message_internal_id uuid not null references messages(internal_id) on delete cascade,
    file_internal_id uuid not null references files(internal_id) on delete cascade,

    primary key (message_internal_id, file_internal_id)
);

create table message_secrets (
    message_internal_id uuid not null references messages(internal_id) on delete cascade,
    client_internal_id uuid not null references clients(internal_id) on delete cascade,

    encrypted_key bytea not null,

    primary key (message_internal_id, client_internal_id)
);

create table message_receipts (
    message_internal_id uuid not null references messages(internal_id) on delete cascade,
    client_internal_id uuid not null references clients(internal_id) on delete cascade,

    delivered_at timestamptz,
    read_at timestamptz,

    primary key (message_internal_id, client_internal_id)
);

create or replace function check_client_is_not_sender() returns trigger language plpgsql
as $$
    begin
        if exists(select 1 from messages where id = new.message_internal_id and sender_internal_id = new.client_internal_id) then
            raise exception 'Cannot add record with client (%) that is sender of this message (%)', new.client_internal_id, new.message_internal_id;
        end if;

        return new;
    end;
$$;

create trigger trg_check_client_is_not_sender before insert on message_receipts
    for each row
    execute function check_client_is_not_sender();

commit;
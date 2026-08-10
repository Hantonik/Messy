start transaction;

create extension if not exists pgcrypto;

create type conversation_type as enum (
    'DIRECT',
    'GROUP'
);

create type content_type as enum (
    'TEXT',
    'FILE',
    'SYSTEM'
);

create table clients (
    id uuid primary key default gen_random_uuid(),

    first_name varchar(255) not null,
    last_name varchar(255) not null,

    email varchar(320) not null,
    phone varchar(20),

    password_hash bytea not null,

    public_key bytea not null,

    encrypted_private_key bytea not null,
    private_key_nonce bytea not null,

    created_at timestamptz not null default now(),
    last_seen_at timestamptz,

    is_deleted boolean not null default false,

    version bigint not null
);

create unique index ux_clients_email_lower on clients(lower(email));
create unique index ux_clients_phone on clients(phone) nulls distinct;

create table client_sessions (
    id uuid primary key default gen_random_uuid(),

    client_id uuid not null references clients(id) on delete cascade,

    token_hash bytea not null,

    created_at timestamptz not null default now(),
    expires_at timestamptz not null,

    last_used_at timestamptz,
    revoked_at timestamptz,

    version bigint not null
);

create unique index ux_client_sessions_token_hash on client_sessions(token_hash);

create index ix_client_sessions_client_id on client_sessions(client_id);
create index ix_client_sessions_expires_at on client_sessions(expires_at);

create table conversations (
    id uuid primary key default gen_random_uuid(),

    type conversation_type not null,

    created_by uuid references clients(id) on delete set null,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    last_message_index integer not null default 0,

    version bigint not null
);

create table conversation_participants (
    conversation_id uuid not null references conversations(id) on delete cascade,
    client_id uuid not null references clients(id) on delete cascade,

    joined_at timestamptz not null default now(),
    left_at timestamptz,

    primary key (conversation_id, client_id)
);

create table direct_conversations (
    conversation_id uuid primary key references conversations(id) on delete cascade,

    client_a_id uuid not null references clients(id) on delete cascade,
    client_b_id uuid not null references clients(id) on delete cascade,

    constraint chk_direct_clients_not_same check (client_a_id <> client_b_id)
);

create unique index ux_direct_conversations_unordered_pair on direct_conversations (
    least(client_a_id, client_b_id),
    greatest(client_a_id, client_b_id)
);

create table group_conversations (
    conversation_id uuid primary key references conversations(id) on delete cascade,

    name varchar(100) not null,
    description text,

    owner_id uuid references clients(id) on delete set null,

    version bigint not null
);

create table group_conversation_secrets (
    conversation_id uuid not null references conversations(id) on delete cascade,
    client_id uuid not null references clients(id) on delete cascade,

    encrypted_key bytea not null,
    key_nonce bytea not null,

    primary key (conversation_id, client_id)
);

create table files (
    id uuid primary key default gen_random_uuid(),

    owner_id uuid references clients(id) on delete set null,

    original_name varchar(255) not null,
    mime_type varchar(255),

    size_bytes bigint not null,
    sha256 bytea not null,

    created_at timestamptz not null default now(),

    version bigint not null
);

create unique index ux_files_sha256 on files(sha256);

create table file_chunks (
    file_id uuid not null references files(id) on delete cascade,
    chunk_index integer not null,

    encrypted_chunk bytea not null,
    chunk_nonce bytea not null,

    primary key (file_id, chunk_index)
);

create table messages (
    id uuid primary key default gen_random_uuid(),
    message_index integer not null,

    conversation_id uuid not null,
    sender_id uuid not null,

    content_type content_type not null,

    encrypted_content bytea,
    content_nonce bytea not null,

    sent_at timestamptz not null default now(),
    deleted_at timestamptz,

    version bigint not null,

    constraint fk_messages_conversation
        foreign key (conversation_id)
        references conversations(id)
        on delete cascade,

    constraint fk_messages_sender
        foreign key (sender_id)
        references clients(id)
        on delete restrict,

    constraint fk_messages_sender_is_participant
        foreign key (conversation_id, sender_id)
        references conversation_participants(conversation_id, client_id)
        on delete restrict
);

create index ix_messages_conversation_sent_at
    on messages(conversation_id, sent_at DESC);
create index ix_messages_sender
    on messages(sender_id);

create unique index ux_messages_conversation_message_index
    on messages(conversation_id, message_index);

create or replace function assign_message_index() returns trigger language plpgsql
as $$
begin
    update conversations set last_message_index = last_message_index + 1, updated_at = now()
    where id = new.conversation_id returning last_message_index into new.message_index;

    if new.message_index is null then
        raise exception 'Conversation with id % does not exist', new.conversation_id;
    end if;

    return new;
end;
$$;

create trigger trg_assign_message_index before insert on messages
    for each row
    execute function assign_message_index();

create table message_attachments (
    message_id uuid not null references messages(id) on delete cascade,
    file_id uuid not null references files(id) on delete cascade,

    primary key (message_id, file_id)
);

create table message_secrets (
    message_id uuid not null references messages(id) on delete cascade,
    client_id uuid not null references clients(id) on delete cascade,

    encrypted_key bytea not null,
    key_nonce bytea not null,

    primary key (message_id, client_id)
);

create table message_receipts (
    message_id uuid not null references messages(id) on delete cascade,
    client_id uuid not null references clients(id) on delete cascade,

    delivered_at timestamptz,
    read_at timestamptz,

    version bigint not null,

    primary key (message_id, client_id)
);

commit;

drop table if exists fwk_transaction_hst;
create table fwk_transaction_hst
(
    tr_dy            date                     not null,
    gid              varchar(255)             not null,
    method           varchar(6),
    path             varchar(200),
    status_code      varchar(3),
    start_time       varchar(6),
    end_time         varchar(6),
    elapsed          varchar(11),
    remote_ip        varchar(20),
    query_string     varchar(4000),
    body             json,
    referrer         varchar(200),
    error_message    varchar(2000),
    create_user_id   varchar(11),
    create_dt        timestamp with time zone not null
);
comment on table fwk_transaction_hst is '거래내역';
create index idx_tr_hst on fwk_transaction_hst (tr_dy);

comment on column fwk_transaction_hst.tr_dy is '거래 일자';
comment on column fwk_transaction_hst.create_user_id is '생성자 ID';
comment on column fwk_transaction_hst.create_dt is '생성일시';

drop table if exists fwk_transaction_hst;
CREATE  TABLE fwk_transaction_hst (
                                             tr_dy                date  NOT NULL  ,
                                             app_nm               varchar(20)  NOT NULL  ,
                                             app_ver              varchar(20)  NOT NULL  ,
                                             gid                  varchar(255)  NOT NULL  ,
                                             "method"             varchar(6)    ,
                                             "path"               varchar(2000)    ,
                                             status_code          varchar(3)    ,
                                             start_time           varchar(6)    ,
                                             end_time             varchar(6)    ,
                                             elapsed              varchar(11)    ,
                                             host_nm              varchar(50)    ,
                                             remote_ip            varchar(20)    ,
                                             query_str            text    ,
                                             body                 text    ,
                                             referrer             varchar(200)    ,
                                             error_msg            varchar(2000)    ,
                                             create_user_id       varchar(11)    ,
                                             create_dt            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_fwk_transaction_hst ON fwk_transaction_hst  ( tr_dy, app_nm, app_ver, gid );
COMMENT ON TABLE fwk_transaction_hst IS '거래내역';
COMMENT ON COLUMN fwk_transaction_hst.tr_dy IS '거래 일자';
COMMENT ON COLUMN fwk_transaction_hst.app_nm IS '어플리케이션 명';
COMMENT ON COLUMN fwk_transaction_hst.app_ver IS '어플리케이션 버전';
COMMENT ON COLUMN fwk_transaction_hst.gid IS '글로벌 ID';
COMMENT ON COLUMN fwk_transaction_hst."method" IS 'Http Method [GET,POST...]';
COMMENT ON COLUMN fwk_transaction_hst."path" IS '요청 경로';
COMMENT ON COLUMN fwk_transaction_hst.status_code IS '응답 코드';
COMMENT ON COLUMN fwk_transaction_hst.start_time IS '시작 시간';
COMMENT ON COLUMN fwk_transaction_hst.end_time IS '종료 시간';
COMMENT ON COLUMN fwk_transaction_hst.elapsed IS '경과 시간';
COMMENT ON COLUMN fwk_transaction_hst.host_nm IS '호스트 명';
COMMENT ON COLUMN fwk_transaction_hst.remote_ip IS '요청지 IP';
COMMENT ON COLUMN fwk_transaction_hst.query_str IS '요청 파라미터';
COMMENT ON COLUMN fwk_transaction_hst.body IS '요청 바디';
COMMENT ON COLUMN fwk_transaction_hst.error_msg IS '에러 메시지';
COMMENT ON COLUMN fwk_transaction_hst.create_user_id IS '생성자 ID';
COMMENT ON COLUMN fwk_transaction_hst.create_dt IS '생성 일시';

drop table if exists "public".com_user_mst;
create sequence com_user_mst_user_id_seq start 1;
CREATE  TABLE "public".com_user_mst (
                                        user_id              bigint DEFAULT nextval('com_user_mst_user_id_seq'::regclass) NOT NULL  ,
                                        create_dt            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
                                        create_pgm_id        varchar(20)    ,
                                        create_user_id       varchar(20)    ,
                                        update_dt            timestamptz    ,
                                        update_pgm_id        varchar(20)    ,
                                        update_user_id       varchar(20)    ,
                                        user_nm              varchar(20)  NOT NULL  ,
                                        user_seq_no          varchar(10)  NOT NULL  ,
                                        CONSTRAINT com_user_mst_pk PRIMARY KEY ( user_id )
);

COMMENT ON TABLE "public".com_user_mst IS '사용자 마스터';
COMMENT ON COLUMN "public".com_user_mst.user_id IS '사용자 ID';
COMMENT ON COLUMN "public".com_user_mst.create_dt IS '생성 일시';
COMMENT ON COLUMN "public".com_user_mst.create_pgm_id IS '생성 프로그램 ID';
COMMENT ON COLUMN "public".com_user_mst.create_user_id IS '생성자 ID';
COMMENT ON COLUMN "public".com_user_mst.update_dt IS '수정 일시';
COMMENT ON COLUMN "public".com_user_mst.update_pgm_id IS '수정 프로그램 ID';
COMMENT ON COLUMN "public".com_user_mst.update_user_id IS '수정자 ID';
COMMENT ON COLUMN "public".com_user_mst.user_nm IS '사용자명';

INSERT INTO "public".com_user_mst ( user_id, user_nm, user_seq_no) VALUES ( default, '홍길동', 'U000012345');
INSERT INTO "public".com_user_mst ( user_id, user_nm, user_seq_no) VALUES ( default, '지천명', 'U000012346');

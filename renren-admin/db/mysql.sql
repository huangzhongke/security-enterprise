-- 系统用户
CREATE TABLE sys_user (
  id bigint NOT NULL COMMENT 'id',
  username varchar(50) NOT NULL COMMENT '用户名',
  password varchar(100) COMMENT '密码',
  real_name varchar(50) COMMENT '姓名',
  head_url varchar(200) COMMENT '头像',
  gender tinyint unsigned COMMENT '性别   0：男   1：女    2：保密',
  email varchar(100) COMMENT '邮箱',
  mobile varchar(100) COMMENT '手机号',
  dept_id bigint COMMENT '部门ID',
  super_admin tinyint unsigned COMMENT '超级管理员   0：否   1：是',
  status tinyint COMMENT '状态  0：停用   1：正常',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  primary key (id),
  unique key uk_username (username),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

-- 部门
CREATE TABLE sys_dept (
  id bigint NOT NULL COMMENT 'id',
  pid bigint COMMENT '上级ID',
  pids varchar(500) COMMENT '所有上级ID，用逗号分开',
  name varchar(50) COMMENT '部门名称',
  sort int unsigned COMMENT '排序',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  primary key (id),
  key idx_pid (pid),
  key idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门管理';

-- 角色管理
create table sys_role
(
  id                   bigint NOT NULL COMMENT 'id',
  name                 varchar(50) COMMENT '角色名称',
  remark               varchar(100) COMMENT '备注',
  dept_id              bigint COMMENT '部门ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  key idx_dept_id (dept_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色管理';

-- 菜单管理
create table sys_menu
(
  id                   bigint NOT NULL COMMENT 'id',
  pid                  bigint COMMENT '上级ID，一级菜单为0',
  url                  varchar(200) COMMENT '菜单URL',
  permissions          varchar(500) COMMENT '授权(多个用逗号分隔，如：sys:user:list,sys:user:save)',
  type                 tinyint unsigned COMMENT '类型   0：菜单   1：按钮',
  icon                 varchar(50) COMMENT '菜单图标',
  sort                 int COMMENT '排序',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  key idx_pid (pid),
  key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='菜单管理';

-- 角色用户关系
create table sys_role_user
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  user_id              bigint COMMENT '用户ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_user_id (user_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色用户关系';

-- 角色菜单关系
create table sys_role_menu
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  menu_id              bigint COMMENT '菜单ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_menu_id (menu_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色菜单关系';

-- 角色数据权限
create table sys_role_data_scope
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  dept_id              bigint COMMENT '部门ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色数据权限';

-- 参数管理
create table sys_params
(
  id                   bigint NOT NULL COMMENT 'id',
  param_code           varchar(32) COMMENT '参数编码',
  param_value          varchar(2000) COMMENT '参数值',
  param_type           tinyint unsigned default 1 COMMENT '类型   0：系统参数   1：非系统参数',
  remark               varchar(200) COMMENT '备注',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  unique key uk_param_code (param_code),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='参数管理';

-- 字典类型
create table sys_dict_type
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type            varchar(100) NOT NULL COMMENT '字典类型',
    dict_name            varchar(255) NOT NULL COMMENT '字典名称',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    UNIQUE KEY(dict_type)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典类型';

-- 字典数据
create table sys_dict_data
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type_id         bigint NOT NULL COMMENT '字典类型ID',
    dict_label           varchar(255) NOT NULL COMMENT '字典标签',
    dict_value           varchar(255) COMMENT '字典值',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    unique key uk_dict_type_value (dict_type_id, dict_value),
    key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典数据';

-- 登录日志
create table sys_log_login
(
  id                   bigint NOT NULL COMMENT 'id',
  operation            tinyint unsigned COMMENT '用户操作   0：用户登录   1：用户退出',
  status               tinyint unsigned NOT NULL COMMENT '状态  0：失败    1：成功    2：账号已锁定',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  creator_name         varchar(50) COMMENT '用户名',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_status (status),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='登录日志';

-- 操作日志
create table sys_log_operation
(
  id                   bigint NOT NULL COMMENT 'id',
  operation            varchar(50) COMMENT '用户操作',
  request_uri          varchar(200) COMMENT '请求URI',
  request_method       varchar(20) COMMENT '请求方式',
  request_params       text COMMENT '请求参数',
  request_time         int unsigned NOT NULL COMMENT '请求时长(毫秒)',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  status               tinyint unsigned NOT NULL COMMENT '状态  0：失败   1：成功',
  creator_name         varchar(50) COMMENT '用户名',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='操作日志';

-- 异常日志
create table sys_log_error
(
  id                   bigint NOT NULL COMMENT 'id',
  request_uri          varchar(200) COMMENT '请求URI',
  request_method       varchar(20) COMMENT '请求方式',
  request_params       text COMMENT '请求参数',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  error_info           text COMMENT '异常信息',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='异常日志';

-- 短信
create table sys_sms
(
    id                   bigint NOT NULL COMMENT 'id',
    sms_code             varchar(32) COMMENT '短信编码',
    platform             tinyint unsigned NOT NULL COMMENT '平台类型',
    sms_config           varchar(2000) COMMENT '短信配置',
    remark               varchar(200) COMMENT '备注',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    unique key uk_sms_code (sms_code),
    key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='短信';

-- 短信日志
CREATE TABLE sys_sms_log (
    id bigint NOT NULL COMMENT 'id',
    sms_code varchar(32) COMMENT '短信编码',
    platform tinyint unsigned NOT NULL COMMENT '平台类型',
    mobile varchar(20) COMMENT '手机号',
    params_1 varchar(50) COMMENT '参数1',
    params_2 varchar(50) COMMENT '参数2',
    params_3 varchar(50) COMMENT '参数3',
    params_4 varchar(50) COMMENT '参数4',
    status tinyint unsigned COMMENT '发送状态  0：失败  1：成功',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    PRIMARY KEY (id),
    key idx_sms_code (sms_code)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='短信日志';

-- 通知管理
CREATE TABLE sys_notice (
    id bigint NOT NULL COMMENT 'id',
    type int NOT NULL COMMENT '通知类型',
    title varchar(200) COMMENT '标题',
    content text COMMENT '内容',
    receiver_type tinyint unsigned COMMENT '接收者  0：全部  1：部门',
    receiver_type_ids varchar(500) COMMENT '接收者ID，用逗号分开',
    status tinyint unsigned COMMENT '发送状态  0：草稿  1：已发布',
    sender_name varchar(50) COMMENT '发送者',
    sender_date datetime COMMENT '发送时间',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    PRIMARY KEY (id),
    key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='通知管理';

-- 我的通知
CREATE TABLE sys_notice_user (
    receiver_id bigint NOT NULL COMMENT '接收者ID',
    notice_id bigint NOT NULL COMMENT '通知ID',
    read_status tinyint unsigned COMMENT '阅读状态  0：未读  1：已读',
    read_date datetime COMMENT '阅读时间',
    PRIMARY KEY (receiver_id, notice_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='我的通知';

-- 邮件模板
CREATE TABLE sys_mail_template (
  id bigint NOT NULL COMMENT 'id',
  name varchar(100) COMMENT '模板名称',
  subject varchar(200) COMMENT '邮件主题',
  content text COMMENT '邮件正文',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='邮件模板';

-- 邮件发送记录
CREATE TABLE sys_mail_log (
  id bigint NOT NULL COMMENT 'id',
  template_id bigint NOT NULL COMMENT '邮件模板ID',
  mail_from varchar(200) COMMENT '发送者',
  mail_to varchar(400) COMMENT '收件人',
  mail_cc varchar(400) COMMENT '抄送者',
  subject varchar(200) COMMENT '邮件主题',
  content text COMMENT '邮件正文',
  status tinyint unsigned COMMENT '发送状态  0：失败  1：成功',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='邮件发送记录';

-- 文件上传
CREATE TABLE sys_oss (
  id bigint NOT NULL COMMENT 'id',
  url varchar(200) COMMENT 'URL地址',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='文件上传';

-- 定时任务
CREATE TABLE schedule_job (
  id bigint NOT NULL COMMENT 'id',
  bean_name varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  params varchar(2000) DEFAULT NULL COMMENT '参数',
  cron_expression varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  status tinyint unsigned COMMENT '任务状态  0：暂停  1：正常',
  remark varchar(255) DEFAULT NULL COMMENT '备注',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务';

-- 定时任务日志
CREATE TABLE schedule_job_log (
  id bigint NOT NULL COMMENT 'id',
  job_id bigint NOT NULL COMMENT '任务id',
  bean_name varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  params varchar(2000) DEFAULT NULL COMMENT '参数',
  status tinyint unsigned NOT NULL COMMENT '任务状态    0：失败    1：成功',
  error varchar(2000) DEFAULT NULL COMMENT '失败信息',
  times int NOT NULL COMMENT '耗时(单位：毫秒)',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  key idx_job_id (job_id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志';

-- 系统用户Token
CREATE TABLE sys_user_token (
  id bigint NOT NULL COMMENT 'id',
  user_id bigint NOT NULL COMMENT '用户id',
  token varchar(100) NOT NULL COMMENT '用户token',
  expire_date datetime COMMENT '过期时间',
  update_date datetime COMMENT '更新时间',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id),
  UNIQUE KEY token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户Token';

-- 国际化
CREATE TABLE sys_language (
  table_name varchar(32) NOT NULL COMMENT '表名',
  table_id bigint NOT NULL COMMENT '表主键',
  field_name varchar(32) NOT NULL COMMENT '字段名',
  field_value varchar(200) NOT NULL COMMENT '字段值',
  language varchar(10) NOT NULL COMMENT '语言',
  primary key (table_name, table_id, field_name, language),
  key idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国际化';

-- 行政区域
CREATE TABLE sys_region (
  id bigint NOT NULL COMMENT 'id',
  pid bigint COMMENT '上级ID，一级为0',
  name varchar(100) COMMENT '名称',
  tree_level tinyint COMMENT '层级',
  leaf tinyint COMMENT '是否叶子节点  0：否   1：是',
  sort bigint COMMENT '排序',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='行政区域';


-- 新闻管理
CREATE TABLE tb_news (
  id bigint NOT NULL COMMENT 'id',
  title varchar(100) NOT NULL COMMENT '标题',
  content mediumtext NOT NULL COMMENT '内容',
  pub_date datetime COMMENT '发布时间',
  dept_id bigint COMMENT '创建者dept_id',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='新闻管理';

CREATE TABLE tb_process_biz_route (
  id bigint(20) NOT NULL COMMENT 'id',
  proc_def_id varchar(64) NOT NULL COMMENT '流程定义ID',
  biz_route varchar(255) NOT NULL COMMENT '业务路由',
  proc_def_key varchar(255) COMMENT '流程定义KEY',
  version int COMMENT '版本号',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='工作流业务路由';

CREATE TABLE tb_correction (
 id bigint(20) NOT NULL COMMENT 'id',
 apply_post varchar(255) COMMENT '申请岗位',
 entry_date datetime COMMENT '入职日期',
 correction_date datetime COMMENT '转正日期',
 work_content varchar(2000) COMMENT '工作内容',
 achievement varchar(2000) COMMENT '工作成绩',
 instance_id varchar(80) COMMENT '实例ID',
 creator bigint COMMENT '创建者',
 create_date datetime COMMENT '创建时间',
 PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='转正申请';

CREATE TABLE tb_product (
    id bigint NOT NULL COMMENT 'id',
    name varchar(100) NOT NULL COMMENT '产品名称',
    content mediumtext NOT NULL COMMENT '产品介绍',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='产品管理';

CREATE TABLE tb_product_params (
    id bigint NOT NULL COMMENT 'id',
    param_name varchar(100) COMMENT '参数名',
    param_value varchar(200) COMMENT '参数值',
    product_id bigint COMMENT '产品ID',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    updater bigint COMMENT '更新者',
    update_date datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='产品参数管理';

-- 初始数据
INSERT INTO sys_user(id, username, password, real_name, gender, email, mobile, status, dept_id, super_admin, creator, create_date, updater, update_date) VALUES (1067246875800000001, 'admin', '$2a$10$012Kx2ba5jzqr9gLlG4MX.bnQJTD9UWqF57XDo2N3.fPtLne02u/m', '管理员', 0, 'root@renren.io', '13612345678', 1, null, 1, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000002, '0', NULL, NULL, 0, 'icon-safetycertificate', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000003, 1067246875800000055, NULL, 'sys:user:save,sys:dept:list,sys:role:list', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000004, 1067246875800000055, NULL, 'sys:user:update,sys:dept:list,sys:role:list', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000005, 1067246875800000055, NULL, 'sys:user:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000006, 1067246875800000055, NULL, 'sys:user:export', 1, NULL, 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000007, 1067246875800000002, 'sys/role', NULL, 0, 'icon-team', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000008, 1067246875800000007, NULL, 'sys:role:page,sys:role:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000009, 1067246875800000007, NULL, 'sys:role:save,sys:menu:select,sys:dept:list', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000010, 1067246875800000007, NULL, 'sys:role:update,sys:menu:select,sys:dept:list', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000011, 1067246875800000007, NULL, 'sys:role:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000012, 1067246875800000002, 'sys/dept', NULL, 0, 'icon-apartment', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000014, 1067246875800000012, NULL, 'sys:dept:list,sys:dept:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000015, 1067246875800000012, NULL, 'sys:dept:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000016, 1067246875800000012, NULL, 'sys:dept:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000017, 1067246875800000012, NULL, 'sys:dept:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000018, 1150940491508928513, 'activiti/process', 'sys:process:all', 0, 'icon-detail', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000019, 1150940491508928513, 'activiti/model', 'sys:model:all', 0, 'icon-appstore-fill', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000020, 1150940491508928513, 'activiti/running', 'sys:running:all', 0, 'icon-play-square', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000021, 1067246875800000024, 'message/sms', 'sys:sms:all', 0, 'icon-message-fill', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000022, 1067246875800000024, 'message/mail_template', 'sys:mail:all', 0, 'icon-mail', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000023, 1067246875800000024, 'message/mail_log', 'sys:mail:log', 0, 'icon-detail-fill', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000024, '0', NULL, NULL, 0, 'icon-message', 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000025, 1067246875800000035, 'sys/menu', NULL, 0, 'icon-unorderedlist', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000026, 1067246875800000025, NULL, 'sys:menu:list,sys:menu:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000027, 1067246875800000025, NULL, 'sys:menu:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000028, 1067246875800000025, NULL, 'sys:menu:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000029, 1067246875800000025, NULL, 'sys:menu:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000030, 1067246875800000035, 'job/schedule', NULL, 0, 'icon-dashboard', 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000031, 1067246875800000030, NULL, 'sys:schedule:page,sys:schedule:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000032, 1067246875800000030, NULL, 'sys:schedule:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000033, 1067246875800000030, NULL, 'sys:schedule:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000034, 1067246875800000030, NULL, 'sys:schedule:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000035, '0', NULL, NULL, 0, 'icon-setting', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000036, 1067246875800000030, NULL, 'sys:schedule:pause', 1, NULL, 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000037, 1067246875800000030, NULL, 'sys:schedule:resume', 1, NULL, 5, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000038, 1067246875800000030, NULL, 'sys:schedule:run', 1, NULL, 6, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000039, 1067246875800000030, NULL, 'sys:schedule:log', 1, NULL, 7, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000040, 1067246875800000035, 'sys/params', '', 0, 'icon-fileprotect', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000041, 1067246875800000035, 'sys/dict-type', NULL, 0, 'icon-golden-fill', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000042, 1067246875800000041, NULL, 'sys:dict:page,sys:dict:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000043, 1067246875800000041, NULL, 'sys:dict:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000044, 1067246875800000041, NULL, 'sys:dict:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000045, 1067246875800000041, NULL, 'sys:dict:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000046, '0', NULL, NULL, 0, 'icon-container', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000047, 1067246875800000035, 'oss/oss', 'sys:oss:all', 0, 'icon-upload', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000048, 1067246875800000046, 'sys/log-login', 'sys:log:login', 0, 'icon-filedone', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000049, 1067246875800000046, 'sys/log-operation', 'sys:log:operation', 0, 'icon-solution', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000050, 1067246875800000046, 'sys/log-error', 'sys:log:error', 0, 'icon-file-exception', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000051, 1067246875800000053, '{{ window.SITE_CONFIG["apiURL"] }}/druid/sql.html', NULL, 0, 'icon-database', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000052, 1067246875800000054, 'demo/news', 'demo:news:all', 0, 'icon-file-word', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000053, '0', NULL, NULL, 0, 'icon-desktop', 5, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000054, '0', NULL, NULL, 0, 'icon-windows', 6, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000055, 1067246875800000002, 'sys/user', NULL, 0, 'icon-user', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000056, 1067246875800000055, NULL, 'sys:user:page,sys:user:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000057, 1067246875800000040, NULL, 'sys:params:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000058, 1067246875800000040, NULL, 'sys:params:export', 1, NULL, 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000059, 1067246875800000040, '', 'sys:params:page,sys:params:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000060, 1067246875800000040, NULL, 'sys:params:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000061, 1067246875800000040, '', 'sys:params:delete', 1, '', 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150940491508928513, '0', NULL, NULL, 0, 'icon-reloadtime', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150941310262235138, '0',  NULL, NULL, 0, 'icon-user', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150941384811794433, 1150941310262235138, 'activiti/my-todo-task', 'sys:task:all', 0, 'icon-dashboard', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150941447038488577, 1150941310262235138, 'activiti/my-join-task', 'sys:his:all', 0, 'icon-check-square', 3, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150941506626965506, 1150940491508928513, 'activiti/process-initiation', 'sys:process:all', 0, 'icon-play-square', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1150941588046794754, 1150941310262235138, 'activiti/my-apply', 'sys:task:all', 0, 'icon-edit-square', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1151384841607028738, 1150941310262235138, 'activiti/my-todo-task-pool', '', 0, 'icon-interation', 5, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1156748733921165314, 1067246875800000053, '{{ window.SITE_CONFIG["apiURL"] }}/doc.html', '', 0, 'icon-file-word', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842114, 1150941310262235138, 'activiti/demo/correction', 'activiti:correction:all', 0, 'icon-issuesclose', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842115, 1067246875800000035, 'sys/region', NULL, 0, 'icon-location', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842116, 1158267114314842115, NULL, 'sys:region:list,sys:region:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842117, 1158267114314842115, NULL, 'sys:region:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842118, 1158267114314842115, NULL, 'sys:region:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1158267114314842119, 1158267114314842115, NULL, 'sys:region:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000300, '0',  NULL, NULL, 0, 'icon-bell', 4, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000301, 1067246875800000300, 'notice/notice', 'sys:notice:all', 0, 'icon-bell', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000302, 1067246875800000300, 'notice/notice-user', 'sys:notice:all', 0, 'icon-notification', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1067246875800000303, 1067246875800000024, 'message/sms-log', 'sys:smslog:all', 0, 'icon-unorderedlist', 0, 1067246875800000001, now(), 1067246875800000001, now());


INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1270380959719501825, 1067246875800000054, 'demo/product', NULL, 0, 'icon-tag', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1270380959719501826, 1270380959719501825, NULL, 'demo:product:page,demo:product:info', 1, NULL, 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1270380959719501827, 1270380959719501825, NULL, 'demo:product:save', 1, NULL, 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1270380959719501828, 1270380959719501825, NULL, 'demo:product:update', 1, NULL, 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_menu(id, pid, url, permissions, type, icon, sort, creator, create_date, updater, update_date) VALUES (1270380959719501829, 1270380959719501825, NULL, 'demo:product:delete', 1, NULL, 3, 1067246875800000001, now(), 1067246875800000001, now());


INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000300, 'name', 'Station Notice', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000300, 'name', '站内通知', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000300, 'name', '站內通知', 'zh-TW');

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', 'Notice Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', '通知管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000301, 'name', '通知管理', 'zh-TW');

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000302, 'name', 'My Notice', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000302, 'name', '我的通知', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000302, 'name', '我的通知', 'zh-TW');

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000303, 'name', 'SMS History', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000303, 'name', '短信发送记录', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000303, 'name', '短信發送記錄', 'zh-TW');

INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1225813644059140097, 'notice_type', '站内通知-类型', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814069634195457, 1225813644059140097, '公告', '0', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814107559092225, 1225813644059140097, '会议', '1', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1225814271879340034, 1225813644059140097, '其他', '2', '', 2, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_sms(id, sms_code, platform, sms_config, remark, creator, create_date, updater, update_date) VALUES (1228954061084676097, '1001', 1, '{"aliyunAccessKeyId":"1","aliyunAccessKeySecret":"1","aliyunSignName":"1","aliyunTemplateCode":"1","qcloudAppKey":"","qcloudSignName":"","qcloudTemplateId":"","qiniuAccessKey":"","qiniuSecretKey":"","qiniuTemplateId":""}', '', 1067246875800000001, now(), 1067246875800000001, now());


INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', 'Authority Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', '权限管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000002, 'name', '權限管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000003, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000004, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000005, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', 'Export', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', '导出', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000006, 'name', '導出', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', 'Role Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', '角色管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000007, 'name', '角色管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000008, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000009, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000010, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000011, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', 'Department Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', '部门管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000012, 'name', '部門管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000014, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000015, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000016, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000017, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', 'Process Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', '流程管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000018, 'name', '流程管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', 'Model Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', '模型管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000019, 'name', '模型管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', 'Running Process', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', '运行中的流程', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000020, 'name', '運行中的流程', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', 'SMS Service', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', '短信服务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000021, 'name', '短信服務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', 'Mail Template', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', '邮件模板', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000022, 'name', '郵件模板', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', 'Mail Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', '邮件发送记录', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000023, 'name', '郵件發送記錄', 'zh-TW ');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', 'Message Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', '消息管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000024, 'name', '消息管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', 'Menu Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', '菜单管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000025, 'name', '菜單管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000026, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000027, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000028, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000029, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', 'Timed Task', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', '定时任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000030, 'name', '定時任務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000031, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000032, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000033, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000034, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', 'Setting', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', '系统设置', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000035, 'name', '系統設置', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', 'Pause', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', '暂停', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000036, 'name', '暫停', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', 'Resume', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', '恢复', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000037, 'name', '恢復', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', 'Execute', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', '立即执行', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000038, 'name', '立即執行', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', 'Log List', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', '日志列表', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000039, 'name', '日誌列表', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', 'Parameter Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', '参数管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000040, 'name', '參數管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', 'Dict Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', '字典管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000041, 'name', '字典管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000042, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000043, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000044, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000045, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', 'Log Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', '日志管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000046, 'name', '日誌管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', 'File Upload', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', '文件上传', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000047, 'name', '文件上傳', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', 'Login Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', '登录日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000048, 'name', '登錄日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', 'Operation Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', '操作日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000049, 'name', '操作日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', 'Error Log', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', '异常日志', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000050, 'name', '異常日誌', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', 'SQL Monitoring', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', 'SQL监控', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000051, 'name', 'SQL監控', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', 'News Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', '新闻管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000052, 'name', '新聞管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', 'System Monitoring', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', '系统监控', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000053, 'name', '系統監控', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', 'Demo', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', '功能示例', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000054, 'name', '功能示例', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', 'User Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', '用户管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000055, 'name', '用戶管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000056, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000057, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', 'Export', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', '导出', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000058, 'name', '導出', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000059, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000060, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000061, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000061, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1067246875800000061, 'name', '刪除', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150940491508928513, 'name', 'Process Management', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150940491508928513, 'name', '流程管理', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150940491508928513, 'name', '流程管理', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941310262235138, 'name', 'Personal Office', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941310262235138, 'name', '个人办公', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941310262235138, 'name', '個人辦公', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941384811794433, 'name', 'My To-do', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941384811794433, 'name', '我的待办', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941384811794433, 'name', '我的待辦', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941447038488577, 'name', 'Task Already', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941447038488577, 'name', '已办任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941447038488577, 'name', '已辦任務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941506626965506, 'name', 'Initiation Process', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941506626965506, 'name', '发起流程', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941506626965506, 'name', '發起流程', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941588046794754, 'name', 'My Application', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941588046794754, 'name', '我的申请', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1150941588046794754, 'name', '我的申請', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1151384841607028738, 'name', 'To Be Signed', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1151384841607028738, 'name', '待签收任务', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1151384841607028738, 'name', '待簽收任務', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1156748733921165314, 'name', 'Interface Document', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1156748733921165314, 'name', '接口文档', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1156748733921165314, 'name', '接口文檔', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842114, 'name', 'Correction Request', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842114, 'name', '转正申请', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842114, 'name', '轉正申請', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842115, 'name', 'Administrative Regions', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842115, 'name', '行政区域', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842115, 'name', '行政區域', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842116, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842116, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842116, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842117, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842117, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842117, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842118, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842118, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842118, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842119, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842119, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1158267114314842119, 'name', '刪除', 'zh-TW');

INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', 'Master And Child', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', '主子表演示', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501825, 'name', '主子表演示', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', 'View', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', '查看', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501826, 'name', '查看', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', 'Add', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', '新增', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501827, 'name', '新增', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', 'Edit', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', '修改', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501828, 'name', '修改', 'zh-TW');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', 'Delete', 'en-US');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', '删除', 'zh-CN');
INSERT INTO sys_language(table_name, table_id, field_name, field_value, language) VALUES ('sys_menu', 1270380959719501829, 'name', '刪除', 'zh-TW');


INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000062, 1067246875800000063, '1067246875800000066,1067246875800000063', '技术部', 2, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000063, 1067246875800000066, '1067246875800000066', '长沙分公司', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000064, 1067246875800000066, '1067246875800000066', '上海分公司', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000065, 1067246875800000064, '1067246875800000066,1067246875800000064', '市场部', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000066, 0, '0', '人人开源集团', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000067, 1067246875800000064, '1067246875800000066,1067246875800000064', '销售部', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dept(id, pid, pids, name, sort, creator, create_date, updater, update_date) VALUES (1067246875800000068, 1067246875800000063, '1067246875800000066,1067246875800000063', '产品部', 1, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_dict_type(id, dict_type, dict_name, remark, sort, creator, create_date, updater, update_date) VALUES (1160061077912858625, 'gender', '性别', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061112075464705, 1160061077912858625, '男', '0', '', 0, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061146967879681, 1160061077912858625, '女', '1', '', 1, 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_dict_data(id, dict_type_id, dict_label, dict_value, remark, sort, creator, create_date, updater, update_date) VALUES (1160061190127267841, 1160061077912858625, '保密', '2', '', 2, 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_params(id, param_code, param_value, param_type, remark, creator, create_date, updater, update_date) VALUES (1067246875800000073, 'CLOUD_STORAGE_CONFIG_KEY', '{"type":1,"qiniuDomain":"http://test.oss.renren.io","qiniuPrefix":"upload","qiniuAccessKey":"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ","qiniuSecretKey":"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV","qiniuBucketName":"renren-oss","aliyunDomain":"","aliyunPrefix":"","aliyunEndPoint":"","aliyunAccessKeyId":"","aliyunAccessKeySecret":"","aliyunBucketName":"","qcloudDomain":"","qcloudPrefix":"","qcloudSecretId":"","qcloudSecretKey":"","qcloudBucketName":""}', '0', '云存储配置信息', 1067246875800000001, now(), 1067246875800000001, now());
INSERT INTO sys_params(id, param_code, param_value, param_type, remark, creator, create_date, updater, update_date) VALUES (1067246875800000075, 'MAIL_CONFIG_KEY', '{"smtp":"smtp.163.com","port":25,"username":"renrenio_test@163.com","password":"renren123456"}', 0, '邮件配置信息', 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO schedule_job (id, bean_name, params, cron_expression, status, remark, creator, create_date, updater, update_date) VALUES (1067246875800000076, 'testTask', 'renren', '0 0/30 * * * ?', 0, '有参测试，多个参数使用json', 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO sys_mail_template(id, name, subject, content, create_date) VALUES (1067246875800000077, '验证码模板', '人人开源注册验证码', '<p>人人开源注册验证码：${code}</p>', now());

--  quartz自带表结构
CREATE TABLE QRTZ_JOB_DETAILS(
  SCHED_NAME VARCHAR(120) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  JOB_CLASS_NAME VARCHAR(250) NOT NULL,
  IS_DURABLE VARCHAR(1) NOT NULL,
  IS_NONCONCURRENT VARCHAR(1) NOT NULL,
  IS_UPDATE_DATA VARCHAR(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13) NULL,
  PREV_FIRE_TIME BIGINT(13) NULL,
  PRIORITY INTEGER NULL,
  TRIGGER_STATE VARCHAR(16) NOT NULL,
  TRIGGER_TYPE VARCHAR(8) NOT NULL,
  START_TIME BIGINT(13) NOT NULL,
  END_TIME BIGINT(13) NULL,
  CALENDAR_NAME VARCHAR(200) NULL,
  MISFIRE_INSTR SMALLINT(2) NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  REPEAT_COUNT BIGINT(7) NOT NULL,
  REPEAT_INTERVAL BIGINT(12) NOT NULL,
  TIMES_TRIGGERED BIGINT(10) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_CRON_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
  TIME_ZONE_ID VARCHAR(80),
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  STR_PROP_1 VARCHAR(512) NULL,
  STR_PROP_2 VARCHAR(512) NULL,
  STR_PROP_3 VARCHAR(512) NULL,
  INT_PROP_1 INT NULL,
  INT_PROP_2 INT NULL,
  LONG_PROP_1 BIGINT NULL,
  LONG_PROP_2 BIGINT NULL,
  DEC_PROP_1 NUMERIC(13,4) NULL,
  DEC_PROP_2 NUMERIC(13,4) NULL,
  BOOL_PROP_1 VARCHAR(1) NULL,
  BOOL_PROP_2 VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_CALENDARS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR BLOB NOT NULL,
  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  ENTRY_ID VARCHAR(95) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  FIRED_TIME BIGINT(13) NOT NULL,
  SCHED_TIME BIGINT(13) NOT NULL,
  PRIORITY INTEGER NOT NULL,
  STATE VARCHAR(16) NOT NULL,
  JOB_NAME VARCHAR(200) NULL,
  JOB_GROUP VARCHAR(200) NULL,
  IS_NONCONCURRENT VARCHAR(1) NULL,
  REQUESTS_RECOVERY VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,ENTRY_ID))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_SCHEDULER_STATE (
  SCHED_NAME VARCHAR(120) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
  CHECKIN_INTERVAL BIGINT(13) NOT NULL,
  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE QRTZ_LOCKS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME VARCHAR(40) NOT NULL,
  PRIMARY KEY (SCHED_NAME,LOCK_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
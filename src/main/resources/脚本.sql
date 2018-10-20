drop table if exists gw_def_flow;
create table gw_def_flow(
	id varchar(36) primary key,
	name varchar(100),
	buss_db_type varchar(100),
	buss_db_host varchar(100),
	buss_db_port varchar(100),
	buss_db_name varchar(100),
	buss_db_user_name varchar(100),
	buss_db_user_pwd varchar(100),
	buss_table varchar(200),
	form_key varchar(500),
	create_user varchar(40),
	create_time datetime,
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);

drop table if exists gw_def_flowbuss;
create table gw_def_flowbuss(
	id varchar(36) primary key,
	flow_id varchar(36),
	column_name varchar(200),
	create_time datetime,
	create_user varchar(40),
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);

drop table if exists gw_def_node;
create table gw_def_node(
	id varchar(36) primary key,
	name varchar(100),
	description varchar(500),
	node_def_id varchar(100),
	flow_id varchar(36),
	is_begin_node char(1),
	is_end_node char(1),
	assign_user varchar(40),
	sort_num integer,
	create_time datetime,
	create_user varchar(40),
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);

drop table if exists gw_def_line;
create table gw_def_line(
	id varchar(36) primary key,
	flow_id varchar(36),
	begin_node_id varchar(36),
	end_node_id varchar(36),
	begin_node_name varchar(100),
	end_node_name varchar(100),
	can_withdraw char(1),
	can_retreat char(1),
	task_type varchar(50),
	finish_type varchar(50),
	exec_type varchar(50),
	create_time datetime,
	create_user varchar(40),
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);


drop table if exists gw_run_procinst;
create table gw_run_procinst(
	id varchar(36) primary key,
	flow_id varchar(36),
	buss_id varchar(36),
	buss_table varchar(100),
	finish_tag char(1),
	finish_time datetime,
	duration long,
	latest_task_id varchar(40),
	latest_task_user varchar(40),
	owner_type varchar(100),
	form_key varchar(500),
	create_user varchar(40),
	create_time datetime,
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);

drop table if exists gw_run_task;
create table gw_run_task(
	id varchar(36) primary key,
	task_type varchar(50),
	proc_inst_id varchar(36),
	flow_id varchar(36),
	node_id varchar(36),
	node_name varchar(200),
	node_def_id varchar(36),

	parent_task_id varchar(36),

	finish_type varchar(40),
	assign_user varchar(36),
	claim_tag char(1),
	claim_user varchar(36),
	owner varchar(36),
	assign_time datetime,
	claim_time datetime,
	pass_str varchar(20),

	finish_tag char(1),
	finish_user varchar(36),
	finish_time datetime,
	duration long,
	description varchar(500),
	withdraw_tag char(1),
	retreat_tag char(1),
	withdraw_description varchar(500),
	retreat_description varchar(500),

	execution_type varchar(200),
	execution_order varchar(1000),

	priority integer,
	lock_tag char(1),
	emergency_tag char(1),


	create_user varchar(40),
	create_time datetime,
	update_user varchar(40),
	update_time datetime,
	del_tag char(1),
	del_reason varchar(500),
	remark varchar(500)
);

/* 1018 增量修改*/
alter table gw_def_flow add create_user varchar(40);
alter table gw_def_flow add update_user varchar(40);
alter table gw_def_flow add update_time datetime;
alter table gw_def_flow add del_tag char(1);
alter table gw_def_flow add del_reason varchar(500);
alter table gw_def_flow add remark varchar(500);

alter table gw_def_flowbuss add create_user varchar(40);
alter table gw_def_flowbuss add update_user varchar(40);
alter table gw_def_flowbuss add update_time datetime;
alter table gw_def_flowbuss add del_tag char(1);
alter table gw_def_flowbuss add del_reason varchar(500);
alter table gw_def_flowbuss add remark varchar(500);

alter table gw_def_node add create_user varchar(40);
alter table gw_def_node add update_user varchar(40);
alter table gw_def_node add update_time datetime;
alter table gw_def_node add del_tag char(1);
alter table gw_def_node add del_reason varchar(500);
alter table gw_def_node add remark varchar(500);

alter table gw_def_line add create_user varchar(40);
alter table gw_def_line add update_user varchar(40);
alter table gw_def_line add update_time datetime;
alter table gw_def_line add del_tag char(1);
alter table gw_def_line add del_reason varchar(500);
alter table gw_def_line add remark varchar(500);

alter table gw_def_flow add form_key varchar(500);
alter table gw_def_node add node_def_id varchar(100);
alter table gw_def_line add task_type varchar(50);
alter table gw_def_line add finish_type varchar(50);
alter table gw_def_line add exec_type varchar(50);
alter table gw_def_node add sort_num integer;

alter table gw_def_flow add buss_db_type varchar(100);
alter table gw_def_flow add buss_db_host varchar(100);
alter table gw_def_flow add buss_db_port varchar(100);
alter table gw_def_flow add buss_db_name varchar(100);
alter table gw_def_flow add buss_db_user_name varchar(100);
alter table gw_def_flow add buss_db_user_pwd varchar(100);

/**20181020*/
update gw_def_node set sort_num = 1 ;
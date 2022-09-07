alter table authorities drop column username;
alter table authorities add constraint PK_Authorities_userid primary key (user_id);

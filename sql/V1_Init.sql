set FOREIGN_KEY_CHECKS = 0;

drop table if exists
  pictures,
  users,
  events,
  event_signups,
  billables,
  event_feedback,
  sponsors,
  committees,
  committee_members,
  subscriptions,
  roles,
  registrations
;

set FOREIGN_KEY_CHECKS = 1;


create table users (
  id bigint not null auto_increment,
  password text not null,
  first_name text not null,
  last_name text not null,
  prefix text,
  initials text,
  address text,
  house_number text,
  postal_code text,
  city text,
  phone_number text,
  email text not null,
  student_number text,
  date_of_birth text,
  created_at datetime(3) not null,
  member_since datetime(3) not null,
  discord text,
  steamid text,
  newsletter bool not null,
  contribution_paid bool not null,
  consent_privacy bool not null,
  consent_gdpr bool not null,
  registration_id bigint, # registration
  profile_picture bigint, # possible picture
  deleted_at datetime(3),
  primary key (id)
);

create table committees (
  id bigint not null auto_increment,
  name text,
  description text,
  primary key (id)
);

create table pictures (
  id bigint not null auto_increment,
  name varchar(255),
  url varchar(255),
  created_at datetime(3),
  uploader_id bigint,
  primary key (id),
  foreign key (uploader_id) references users(id)
);

create table events (
  id bigint not null auto_increment,
  creator_id bigint, # user
  last_editor_id bigint, # user
  committee_id bigint, # committee
  title varchar(255) not null,
  description varchar(255) not null,
  visibility varchar(255) not null, # Enum
  location varchar(255),
  start_time datetime(3),
  banner_id bigint, # picture
  price_member double,
  price_public double,
  primary key (id),
  foreign key (creator_id) references users(id),
  foreign key (last_editor_id) references users(id),
  foreign key (committee_id) references committees(id)
);

create table event_signups (
  event_id bigint,
  user_id bigint,
  options text,
  primary key (event_id, user_id)
);

create table billables (
  id bigint not null auto_increment,
  source_id bigint, # user
  description text,
  quantity int,
  price double,
  paid bool,
  event_id bigint, # possible event
  created_at datetime(3),
  primary key (id),
  foreign key (source_id) references users(id)
);

create table event_feedback (
  id bigint not null auto_increment,
  feedback text,
  event_id bigint, # event
  primary key (id),
  foreign key (event_id) references events(id)
);

create table sponsors (
  id bigint not null auto_increment,
  name text,
  description text,
  logo_id bigint, # possible picture
  primary key (id)
);

create table committee_members (
  user_id bigint,
  committee_id bigint,
  role text,
  primary key (user_id, committee_id)
);

create table subscriptions (
  user_id bigint,
  committee_id bigint,
  primary key (user_id, committee_id)
);

create table roles (
  user_id bigint,
  role varchar(255), # enum
  primary key (user_id, role)
);

create table registrations (
  id bigint not null auto_increment,
  user_id bigint,
  registration_state text, # enum
  accepted_by_user_id bigint, # user
  accepted_at datetime(3),
  primary key (id),
  foreign key (user_id) references users(id),
  foreign key (accepted_by_user_id) references users(id)
)





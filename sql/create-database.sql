create database favorites;
set database = favorites;
create table favorites (
  username string not null,
  beer_id int not null,
  primary key (username, beer_id)
);

use hs_decks;
set sql_safe_updates = 0;

create table classes(
	id int primary key auto_increment,
    name varchar(20) not null unique,
    games_played int default(0) not null, 
		constraint class_games_pos check (games_played >= 0),
    winrate double default(0) not null, 
		constraint class_rate_perc check (winrate >= 0 and winrate <= 100)
);

create table decks(
	id int primary key auto_increment,
    name varchar(30) not null,
        index name_index (name),
    author varchar(30),
    class_id int not null, foreign key (class_id) references classes (id),
    dust_cost int not null,
		constraint cost_pos check (dust_cost > 0),
    games_played int not null, 
		constraint games_pos check (games_played > 0),
    games_won int not null, 
		constraint won_less_than_total check (games_won <= games_played),
    winrate double not null,
		constraint rate_perc check (winrate >= 0 and winrate <= 100)
);

# триггеры и вспомогательные процедуры

create trigger ins_wr before insert on decks for each row 
	set new.winrate = new.games_won / new.games_played;
    
create trigger upd_wr before update on decks for each row 
	set new.winrate = new.games_won / new.games_played;

create procedure wr_deck_added(
	new_class_id int,
    new_games_played int,
    new_games_won int
)
update classes
	set winrate = (winrate * games_played + new_games_won) / (games_played + new_games_played),
		games_played = games_played + new_games_played
	where classes.id = new_class_id;

create procedure wr_deck_removed(
	old_class_id int,
    old_games_played int,
    old_games_won int
)
update classes
	set winrate = case
		when games_played > old_games_played then 
			(winrate * games_played - old_games_won) / (games_played - old_games_played)
		when games_played = old_games_played then 0 
        end,
		games_played = games_played - old_games_played
	where classes.id = old_class_id;

create trigger ins_class after insert on decks for each row 
	call wr_deck_added(new.class_id, new.games_played, new.games_won);

delimiter $$
create trigger upd_class after update on decks for each row 
begin
	call wr_deck_added(new.class_id, new.games_played, new.games_won);
	call wr_deck_removed(old.class_id, old.games_played, old.games_won);		
end$$
delimiter ;

create trigger del_class after delete on decks for each row 
	call wr_deck_removed(old.class_id, old.games_played, old.games_won);


# процедуры, вызываемые из приложения

create procedure get_table_decks()
	select * from decks;

delimiter $$
create procedure clear_table_decks()
begin
	delete from decks;
    alter table decks auto_increment = 1;
end$$
delimiter ;

create procedure insert_deck(
	in in_name varchar(30),
    in in_author varchar(30),
    in in_class_id int,
    in in_dust_cost int,
    in in_games_played int,
    in in_games_won int)
    insert into decks(name, author, class_id, dust_cost, games_played, games_won)
		values (in_name, in_author, in_class_id, in_dust_cost, in_games_played, in_games_won);

create procedure update_deck(
	in in_id int,
	in in_name varchar(30),
    in in_author varchar(30),
    in in_class_id int,
    in in_dust_cost int,
    in in_games_played int,
    in in_games_won int)
    update decks set
		name = in_name,
        author = in_author,
        class_id = in_class_id,
        dust_cost = in_dust_cost,
        games_played = in_games_played,
        games_won = in_games_won
	where id = in_id;

create procedure delete_deck(in in_id int)
	delete from decks where id = in_id;

create procedure select_decks_by_name(in in_name varchar(30))
	select * from decks where name = in_name;

create procedure delete_decks_by_name(in in_name varchar(30))
	delete from decks where name = in_name;



create procedure get_table_classes()
	select * from classes;

delimiter $$
create procedure clear_table_classes()
begin
	delete from classes;
    alter table classes auto_increment = 1;
end$$
delimiter ;

create procedure insert_class(in in_name varchar(20))
	insert into classes (name) value(in_name);

create procedure update_class(
	in in_id int,
	in in_name varchar(20))
	update classes set name = in_name where id = in_id;

create procedure delete_class(in in_id int)
	delete from classes where id = in_id;
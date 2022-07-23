create table daily_sequence (
                                day date, s integer, primary key (day, s)
);

create or replace function daily_sequence()
    returns int as $$
     with d as (
         delete from daily_sequence
             where day < current_date
     )
   insert into daily_sequence (day, s)
   select current_date, coalesce(max(s), 0) + 1
     from daily_sequence
    where day = current_date
returning s
    ;
$$ language sql;

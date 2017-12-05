set dateformat ymd
--//********Задача 1********//
--Ответ:
--Процесс 2 (ReadCommited) ничего не прочитает пока не завершится транзакция процесса 1.
--Процесс 3 (ReadUncommited) считает данные равные 1.

--//********Задача 2********//
declare @tblBooks table(id int, name varchar(50))
insert into @tblBooks
select 1, 'Война и мир'
union all
select 2, 'Преступление и наказание'
union all
select 3, 'Мастер и Маргарита'
union all
select 4, 'Тихий дон'

declare @tblBookInLibrary table(bookid int, date datetime)
insert into @tblBookInLibrary
select 1, '2006-05-01'
union all
select 3, '2004-07-05'

--1)Выбрать все книги, а поле дата должно быть заполнено только у тех книг, у которых дата регистрации больше  01.02.2005
select a.id, a.name, case when b.date is not null and b.date > '2005-02-01' then b.date else null end as date  
from @tblBooks a
left join @tblBookInLibrary b
on a.id = b.bookid

----2)Выбрать все книги у которых дата регистрации в библиотеке больше  01.02.2005 либо не задана вообще:
select a.id, a.name, b.date 
from @tblBooks a
left join @tblBookInLibrary b
on a.id = b.bookid
where (b.date is not null and b.date > '2005-02-01') or b.date is null


--//********Задача 3********//
declare @tblAccounts table(CounterpartyID int IDENTITY(1,1) PRIMARY KEY, Name varchar(255), IsActive bit)
insert into @tblAccounts select 'Иванов',1 union all select 'Петров',0 union all select 'Сидоров',1

declare @tblTransactions table(TransID int,TransDate datetime,RcvID int,SndID int,AssetID int,Quantity numeric(19, 8))
insert into @tblTransactions
select 1,'2012-01-01',1,2,1,100
union all
select 2,'2012-01-02',1,3,2,150
union all 
select 3,'2012-01-03',3,1,1,300
union all 
select 4,'2012-01-04',2,1,3,50

--1)Отобрать активные счета по которым есть проводки как минимум по двум разным активам. 
--Выводимые поля: CounterpartyID, Name, Cnt(количество уникальных активов по которым есть проводки)
select a.CounterpartyID, a.Name, COUNT(DISTINCT b.AssetID) as Cnt from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID or a.CounterpartyID = b.SndID
where a.IsActive = 1
group by a.CounterpartyID,a.Name
having COUNT(DISTINCT b.AssetID) >= 2
 
--2)	Посчитать суммарное число актива, образовавшееся на активных счетах, 
--в результате проведенных проводок. Выводимые поля: CounterpartyID, Name, AssetID, Quantity 
select a.CounterpartyID, a.Name, b.AssetID, SUM(b.Quantity) as Quantity from
@tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID 
where a.IsActive = 1
group by a.CounterpartyID, b.AssetID, a.Name

--3)	Посчитать средний дневной оборот по всем счетам по всем проводкам считая что AssetID 
--во всех проводках одинаковый. Выводимые поля: CounterpartyID, Name, Oborot
select a.CounterpartyID, a.Name, SUM(b.Quantity)/COUNT(distinct b.TransDate) as Oborot from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID
group by a.CounterpartyID, a.Name


--4)	Посчитать средний месячный оборот по всем счетам по всем проводкам считая 
--что AssetID во всех проводках одинаковый. Выводимые поля: CounterpartyID, Name, Oborot
select a.CounterpartyID, a.Name, SUM(b.Quantity)/COUNT(distinct MONTH(b.TransDate)) as Oborot from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID
group by a.CounterpartyID, a.Name

--//********Задача 4********//
declare @tblPeriods table(id int PRIMARY KEY, value int)
insert into @tblPeriods
select 1,	10
union all
select 3,	10
union all
select 5,	20
union all
select 6,	20
union all
select 7,	30
union all
select 9,	40
union all
select 10,	40

--1)	Требуется отобрать периоды в которых значение Value отличается от значения Value в предыдущем периоде. 
--Выводимые поля: PeriodID, Value. В примере выше должны быть выведены значения 1, 5, 7, 9
select MIN(a.id) as id from @tblPeriods a
group by a.value


--2)	Требуется удалить из таблицы периоды в которых значение Value равно значению Value в предыдущем периоде. 
--Выводимые поля: PeriodID, Value. В примере выше должны быть удалены значения 3, 6, 10

--вариант №1 удаляет в которых значение Value равно значению Value в предыдущем периоде. 
--delete from @tblPeriods where id in (
	select MAX(a.id) as id from @tblPeriods a
	group by a.value
	having COUNT(a.value) > 1
--)

--вариант №2 удаляет все повторения
--delete from @tblPeriods where id in (
	select a.id 
	from (
			select  a.id, MIN(a.id) OVER(PARTITION BY a.value) as minId, COUNT(a.id) OVER(PARTITION BY a.value) AS countId 
			from @tblPeriods a
		) a 
	where a.id <> a.minId and countId > 1
--)
go

--//********Задача 5********//
--Дано: 
--Поезд замкнутый (каждый вагон сцеплен с двумя другими). 
--Можно представлять, что он стоит на замкнутых круглых путях. 
--В начальный момент времени наблюдатель (тот, кто считает вагоны) находится перед одним из вагонов (неизвестно каким). 
--В каждый момент времени наблюдатель видит только один вагон. Наблюдатель может двигаться вдоль поезда и включать/выключать свет в вагонах. 
--Все вагоны одинаковые. Изначально свет в каждом конкретном вагоне может гореть, а может и не гореть – это заранее неизвестно.
--Нужно: 
--Посчитать количество вагонов в поезде.

--Ответ:
set nocount on
--Создадим таблицу вагонов, идентификатор вагона, ссылка на предыдущий вагон, ссылка на следующий вагон, светВключен(да/нет) 
declare @Vagons table (id int primary key, prevID int, nextID int, lightOn bit)

declare @VagonsCount int
--сгенерируем случайное число вагонов в поезде
set @VagonsCount  = cast((rand(checksum(newid())) * 5000)as int);

-- сгенерируем таблицу вагонов
with table_cte(id, prevID, nextID, data)
as
(
	select 1 as id, @VagonsCount as prevID, 2 as nextID, cast((rand(checksum(newid())) * 2) as int) as lightOn
	union all
	select id + 1, case when prevID <= @VagonsCount then id else prevID - 1 end,case when @VagonsCount <= nextID then 1 else nextID + 1 end, cast((rand(checksum(newid())) * 2) as int)
	from table_cte
	where id < @VagonsCount 
)

insert into @Vagons
select id, prevID, nextID, data from table_cte OPTION (MAXRECURSION 5000)

--отобразим сгенерированную таблицу вагонов
select * from @Vagons

declare 
	@PointerID int, -- указатель на текущий вагон 
	@RootID int,	-- текущий первый выбранный вагон  
	@Counter int	-- счетчик вагонов

--выберем первый случайный вагон
set @PointerID = cast((rand(checksum(newid())) * @VagonsCount + 1) as int)
set @RootID = @PointerID
set @Counter = 0

--проверяем свет включен в первом выбраном вагоне ?
if not exists(select lightOn from @Vagons where id = @RootID and lightOn = 1)
begin
	--включаем свет в первом выбраном вагоне
	update @Vagons set lightOn = 1 where id = @RootID
end

declare @CurrentLightOn bit;
set @CurrentLightOn = 0;

while 1=1
begin
	--двигаемся в выбранном направлении до первого вагона с включенным светом.
	select @PointerID = nextID, @CurrentLightOn = lightOn from @Vagons where id = @PointerID
	if @@ROWCOUNT = 0 break;

	--считаем количество пройденных вагонов
	set @Counter = @Counter + 1
	--если свет включен в текущем вагоне, то 
	if @CurrentLightOn = 1
	begin
		--выключим свет в текущем вагоне
		update @Vagons set lightOn = 0 where id = @PointerID
		--проверим что со светом в нашем первом выбраном вагоне, если он выключен, то мы достигли конца поезда и остановим движение по вагонам
		if not exists(select lightOn from @Vagons where id = @RootID and lightOn = 1) break;
		--включим свет в текущем вагоне
		update @Vagons set lightOn = 1 where id = @PointerID
	end
	--продолжаем двигаться вперед по поезду
end
--Всего вагонов в поезде, идентификатор первого выбранного вагона:
select 'Total vagons: ' + cast(@Counter as varchar(100)) as TotalVagons, @RootID as FirstRootVagon
set nocount off
set dateformat ymd
--//********������ 1********//
--�����:
--������� 2 (ReadCommited) ������ �� ��������� ���� �� ���������� ���������� �������� 1.
--������� 3 (ReadUncommited) ������� ������ ������ 1.

--//********������ 2********//
declare @tblBooks table(id int, name varchar(50))
insert into @tblBooks
select 1, '����� � ���'
union all
select 2, '������������ � ���������'
union all
select 3, '������ � ���������'
union all
select 4, '����� ���'

declare @tblBookInLibrary table(bookid int, date datetime)
insert into @tblBookInLibrary
select 1, '2006-05-01'
union all
select 3, '2004-07-05'

--1)������� ��� �����, � ���� ���� ������ ���� ��������� ������ � ��� ����, � ������� ���� ����������� ������  01.02.2005
select a.id, a.name, case when b.date is not null and b.date > '2005-02-01' then b.date else null end as date  
from @tblBooks a
left join @tblBookInLibrary b
on a.id = b.bookid

----2)������� ��� ����� � ������� ���� ����������� � ���������� ������  01.02.2005 ���� �� ������ ������:
select a.id, a.name, b.date 
from @tblBooks a
left join @tblBookInLibrary b
on a.id = b.bookid
where (b.date is not null and b.date > '2005-02-01') or b.date is null


--//********������ 3********//
declare @tblAccounts table(CounterpartyID int IDENTITY(1,1) PRIMARY KEY, Name varchar(255), IsActive bit)
insert into @tblAccounts select '������',1 union all select '������',0 union all select '�������',1

declare @tblTransactions table(TransID int,TransDate datetime,RcvID int,SndID int,AssetID int,Quantity numeric(19, 8))
insert into @tblTransactions
select 1,'2012-01-01',1,2,1,100
union all
select 2,'2012-01-02',1,3,2,150
union all 
select 3,'2012-01-03',3,1,1,300
union all 
select 4,'2012-01-04',2,1,3,50

--1)�������� �������� ����� �� ������� ���� �������� ��� ������� �� ���� ������ �������. 
--��������� ����: CounterpartyID, Name, Cnt(���������� ���������� ������� �� ������� ���� ��������)
select a.CounterpartyID, a.Name, COUNT(DISTINCT b.AssetID) as Cnt from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID or a.CounterpartyID = b.SndID
where a.IsActive = 1
group by a.CounterpartyID,a.Name
having COUNT(DISTINCT b.AssetID) >= 2
 
--2)	��������� ��������� ����� ������, �������������� �� �������� ������, 
--� ���������� ����������� ��������. ��������� ����: CounterpartyID, Name, AssetID, Quantity 
select a.CounterpartyID, a.Name, b.AssetID, SUM(b.Quantity) as Quantity from
@tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID 
where a.IsActive = 1
group by a.CounterpartyID, b.AssetID, a.Name

--3)	��������� ������� ������� ������ �� ���� ������ �� ���� ��������� ������ ��� AssetID 
--�� ���� ��������� ����������. ��������� ����: CounterpartyID, Name, Oborot
select a.CounterpartyID, a.Name, SUM(b.Quantity)/COUNT(distinct b.TransDate) as Oborot from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID
group by a.CounterpartyID, a.Name


--4)	��������� ������� �������� ������ �� ���� ������ �� ���� ��������� ������ 
--��� AssetID �� ���� ��������� ����������. ��������� ����: CounterpartyID, Name, Oborot
select a.CounterpartyID, a.Name, SUM(b.Quantity)/COUNT(distinct MONTH(b.TransDate)) as Oborot from @tblAccounts a
left join @tblTransactions b on a.CounterpartyID = b.RcvID
group by a.CounterpartyID, a.Name

--//********������ 4********//
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

--1)	��������� �������� ������� � ������� �������� Value ���������� �� �������� Value � ���������� �������. 
--��������� ����: PeriodID, Value. � ������� ���� ������ ���� �������� �������� 1, 5, 7, 9
select MIN(a.id) as id from @tblPeriods a
group by a.value


--2)	��������� ������� �� ������� ������� � ������� �������� Value ����� �������� Value � ���������� �������. 
--��������� ����: PeriodID, Value. � ������� ���� ������ ���� ������� �������� 3, 6, 10

--������� �1 ������� � ������� �������� Value ����� �������� Value � ���������� �������. 
--delete from @tblPeriods where id in (
	select MAX(a.id) as id from @tblPeriods a
	group by a.value
	having COUNT(a.value) > 1
--)

--������� �2 ������� ��� ����������
--delete from @tblPeriods where id in (
	select a.id 
	from (
			select  a.id, MIN(a.id) OVER(PARTITION BY a.value) as minId, COUNT(a.id) OVER(PARTITION BY a.value) AS countId 
			from @tblPeriods a
		) a 
	where a.id <> a.minId and countId > 1
--)
go

--//********������ 5********//
--����: 
--����� ��������� (������ ����� ������� � ����� �������). 
--����� ������������, ��� �� ����� �� ��������� ������� �����. 
--� ��������� ������ ������� ����������� (���, ��� ������� ������) ��������� ����� ����� �� ������� (���������� �����). 
--� ������ ������ ������� ����������� ����� ������ ���� �����. ����������� ����� ��������� ����� ������ � ��������/��������� ���� � �������. 
--��� ������ ����������. ���������� ���� � ������ ���������� ������ ����� ������, � ����� � �� ������ � ��� ������� ����������.
--�����: 
--��������� ���������� ������� � ������.

--�����:
set nocount on
--�������� ������� �������, ������������� ������, ������ �� ���������� �����, ������ �� ��������� �����, �����������(��/���) 
declare @Vagons table (id int primary key, prevID int, nextID int, lightOn bit)

declare @VagonsCount int
--����������� ��������� ����� ������� � ������
set @VagonsCount  = cast((rand(checksum(newid())) * 5000)as int);

-- ����������� ������� �������
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

--��������� ��������������� ������� �������
select * from @Vagons

declare 
	@PointerID int, -- ��������� �� ������� ����� 
	@RootID int,	-- ������� ������ ��������� �����  
	@Counter int	-- ������� �������

--������� ������ ��������� �����
set @PointerID = cast((rand(checksum(newid())) * @VagonsCount + 1) as int)
set @RootID = @PointerID
set @Counter = 0

--��������� ���� ������� � ������ �������� ������ ?
if not exists(select lightOn from @Vagons where id = @RootID and lightOn = 1)
begin
	--�������� ���� � ������ �������� ������
	update @Vagons set lightOn = 1 where id = @RootID
end

declare @CurrentLightOn bit;
set @CurrentLightOn = 0;

while 1=1
begin
	--��������� � ��������� ����������� �� ������� ������ � ���������� ������.
	select @PointerID = nextID, @CurrentLightOn = lightOn from @Vagons where id = @PointerID
	if @@ROWCOUNT = 0 break;

	--������� ���������� ���������� �������
	set @Counter = @Counter + 1
	--���� ���� ������� � ������� ������, �� 
	if @CurrentLightOn = 1
	begin
		--�������� ���� � ������� ������
		update @Vagons set lightOn = 0 where id = @PointerID
		--�������� ��� �� ������ � ����� ������ �������� ������, ���� �� ��������, �� �� �������� ����� ������ � ��������� �������� �� �������
		if not exists(select lightOn from @Vagons where id = @RootID and lightOn = 1) break;
		--������� ���� � ������� ������
		update @Vagons set lightOn = 1 where id = @PointerID
	end
	--���������� ��������� ������ �� ������
end
--����� ������� � ������, ������������� ������� ���������� ������:
select 'Total vagons: ' + cast(@Counter as varchar(100)) as TotalVagons, @RootID as FirstRootVagon
set nocount off
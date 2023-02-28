PRAGMA foreign_keys = ON;

CREATE TABLE "days" (
	"day_id"	INTEGER NOT NULL,
	"data"	INTEGER NOT NULL UNIQUE,
	PRIMARY KEY("day_id" AUTOINCREMENT)
);

CREATE TABLE "tasks" (
	"task_id"	INTEGER NOT NULL UNIQUE,
	"task"	TEXT NOT NULL,
	"is_done"	INTEGER NOT NULL DEFAULT 0,
	"day_id_key"	INTEGER NOT NULL,
	"order_in_list" INTEGER NOT NULL,
	FOREIGN KEY("day_id_key") REFERENCES "days"("day_id") ON UPDATE CASCADE ON DELETE CASCADE,
	PRIMARY KEY("task_id" AUTOINCREMENT)
);
const { DataTypes } = require("sequelize");

const sequelize = require("../config");
const Schedule = require("./schedule.model");
const Location = require("./location.model");

const SchedulesLocations = sequelize.define(
  "SchedulesLocations",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      allowNull: false,
    },
    position: {
      type: DataTypes.INTEGER,
      allowNull: false,
      defaultValue: 0,
    },
    note: {
      type: DataTypes.TEXT,
      allowNull: true,
      defaultValue: "",
    },
  },
  {
    tableName: "schedules_locations",
    onDelete: "CASCADE",
  }
);

Schedule.belongsToMany(Location, {
  through: {
    model: SchedulesLocations,
    unique: false,
  },
  foreignKey: "schedule_id",

  as: "locations",
});

Location.belongsToMany(Schedule, {
  through: {
    model: SchedulesLocations,
    unique: false,
  },
  foreignKey: "location_id",
  as: "schedules",
});

module.exports = SchedulesLocations;

const { DataTypes } = require("sequelize");

const sequelize = require("../config");

const Converter = require("../utils/converter");

const Schedule = sequelize.define(
    "Schedule",
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true,
            allowNull: false,
        },
        title: {
            type: DataTypes.TEXT,
            defaultValue: "Untitled",
        },
        date: {
            type: DataTypes.DATEONLY,
            defaultValue: DataTypes.NOW,
            allowNull: false,
        },
        location_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
    },
    {
        tableName: "schedules",
        hooks: {
            beforeCreate: (schedule) => {
                schedule.title = Converter.toScheduleTitle(schedule.date);
            },
            beforeUpdate: (schedule) => {
                schedule.title = Converter.toScheduleTitle(schedule.date);
            },
        },
    }
);

sequelize.models.Post.hasMany(Schedule, {
    foreignKey: "post_id",
    as: "schedules",
    onDelete: "CASCADE",
});

Schedule.belongsTo(sequelize.models.Post, {
    foreignKey: "post_id",
    as: "post",
    onDelete: "CASCADE",
});

module.exports = Schedule;

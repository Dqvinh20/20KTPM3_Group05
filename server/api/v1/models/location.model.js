const { DataTypes } = require("sequelize");

const sequelize = require("../config");
const Converter = require("../utils/converter");

const Location = sequelize.define(
  "Location",
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
    title_non_accent: {
      type: DataTypes.TEXT,
    },
    place_id: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
    geo: {
      type: DataTypes.GEOMETRY("POINT", 4326),
      allowNull: false,
    },
    address: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
    photo: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
  },
  {
    tableName: "locations",
    hooks: {
      beforeCreate: (location) => {
        location.title_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          location.title
        );
      },
      beforeUpdate: (location) => {
        location.title_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          location.title
        );
      },
    },
    onDelete: "CASCADE",
  }
);

module.exports = Location;
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
    name: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    name_non_accent: {
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
    formatted_address: {
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
        location.name_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          location.name
        );
      },
      beforeUpdate: (location) => {
        location.name_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          location.name
        );
      },
    },
    onDelete: "CASCADE",
  }
);

module.exports = Location;

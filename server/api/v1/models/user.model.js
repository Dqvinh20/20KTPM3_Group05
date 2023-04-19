const { DataTypes } = require("sequelize");
const bcrypt = require("bcrypt");

const sequelize = require("../config");
const Converter = require("../utils/converter");

const encryptPassword = function (password) {
  const salt = bcrypt.genSaltSync(12);
  const hash = bcrypt.hashSync(password, salt);
  return hash;
};

const User = sequelize.define(
  "User",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      allowNull: false,
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
    },
    password: {
      type: DataTypes.STRING,
      allowNull: false,
      set(value) {
        this.setDataValue("password", encryptPassword(value));
      },
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    user_name: {
      type: DataTypes.STRING,
      allowNull: false,
      defaultValue: "user " + Math.floor(Math.random() * 1000000),
    },
    name_non_accent: {
      type: DataTypes.STRING,
    },
    avatar: {
      type: DataTypes.STRING,
      defaultValue:
        "https://res.cloudinary.com/dkzlalahi/image/upload/v1678277295/default_male_avatar.jpg",
    },
    followers_count: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
    },
    following_count: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
    },
  },
  {
    tableName: "users",
    hooks: {
      beforeCreate: async (user) => {
        user.name_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          user.name
        );
      },
      beforeUpdate: async (user) => {
        user.name_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          user.name
        );
      },
      afterCreate: async (user) => {
        user.user_name = "user" + user.id;
        await user.save();
      },
    },
  }
);

User.belongsToMany(User, {
  as: "followers",
  through: "user_followers",
  foreignKey: "following_id",
  otherKey: "follower_id",
});

User.belongsToMany(User, {
  as: "followings",
  through: "user_followers",
  foreignKey: "follower_id",
  otherKey: "following_id",
});

User.prototype.checkPassword = function (plainPass, hashPass) {
  return bcrypt.compareSync(plainPass, hashPass);
};

module.exports = User;

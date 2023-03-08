const { DataTypes } = require("sequelize");
const sequelize = require("../config");
const bcrypt = require("bcrypt");

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
        avatar: {
            type: DataTypes.STRING,
            defaultValue: "https://i.imgur.com/1Q9ZQ9r.png", // hinh test
        },
        followers_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
        following_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
        tokens: {
            type: DataTypes.STRING,
            defaultValue: "",
        },
    },
    {
        tableName: "users",
    }
);

User.prototype.checkPassword = function (plainPass, hashPass) {
    return bcrypt.compareSync(plainPass, hashPass);
};

module.exports = User;

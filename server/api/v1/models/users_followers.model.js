const { DataTypes } = require("sequelize");
const sequelize = require("../config");

const User = require("./user.model");
const UserFlower = sequelize.define(
    "UserFlower",
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true,
            allowNull: false,
        },

        following_id: {
            type: DataTypes.INTEGER,
            allowNull: false,
        },
    },

    { tableName: "users_followers" }
);

User.belongsToMany(User, {
    through: UserFlower,
    as: "followers",
    foreignKey: "following_id",
    otherKey: "follower_id",
});

module.exports = UserFlower;

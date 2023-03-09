const { DataTypes } = require("sequelize");
const sequelize = require("../config");

const User = require("./user.model");

const Post = sequelize.define(
    "Post",
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true,
            allowNull: false,
        },
        title: {
            type: DataTypes.STRING,
        },
        brief_description: {
            type: DataTypes.STRING,
        },
        cover_img: {
            type: DataTypes.STRING,
            defaultValue: "",
        },
    },
    { tableName: "posts" }
);

User.hasMany(Post, {
    foreignKey: {
        name: "created_by",
        allowNull: false,
    },
});

Post.belongsTo(User, {
    foreignKey: {
        name: "created_by",
        allowNull: false,
    },
});

module.exports = Post;

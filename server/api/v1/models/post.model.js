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
      type: DataTypes.TEXT,
    },
    brief_description: {
      type: DataTypes.TEXT,
      defaultValue: "",
    },
    cover_img: {
      type: DataTypes.STRING,
      allowNull: false,
      defaultValue:
        "https://res.cloudinary.com/dkzlalahi/image/upload/q_90/v1681707145/default_trip_plan_cover_img.png",
    },
    is_public: {
      type: DataTypes.BOOLEAN,
      defaultValue: true,
    },
    view_count: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
    },
    like_count: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
    },
    avg_rating: {
      type: DataTypes.DOUBLE,
      defaultValue: 0,
    },
    rating_count: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
    },
    start_date: {
      type: DataTypes.DATEONLY,
      defaultValue: DataTypes.NOW,
    },
    end_date: {
      type: DataTypes.DATEONLY,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    tableName: "posts",
  }
);

User.hasMany(Post, {
  as: "owned_posts",
  foreignKey: {
    name: "created_by",
    allowNull: false,
  },
});

Post.belongsTo(User, {
  as: "author",
  foreignKey: {
    name: "created_by",
    allowNull: false,
  },
});

Post.belongsToMany(User, {
  through: "users_posts_like",
  as: "liked_by",
  foreignKey: "post_id",
  otherKey: "user_id",
});

User.belongsToMany(Post, {
  through: "users_posts_like",
  as: "liked_posts",
  foreignKey: "user_id",
  otherKey: "post_id",
});

module.exports = Post;

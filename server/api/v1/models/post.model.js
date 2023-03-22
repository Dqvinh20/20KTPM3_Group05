const { DataTypes } = require("sequelize");

const sequelize = require("../config");
const User = require("./user.model");
const Converter = require("../utils/converter");

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
    title_non_accent: {
      type: DataTypes.TEXT,
    },
    brief_description: {
      type: DataTypes.TEXT,
    },
    cover_img: {
      type: DataTypes.STRING,
      defaultValue:
        "https://res.cloudinary.com/dkzlalahi/image/upload/v1677847284/cld-sample-2.jpg",
    },
    is_public: {
      type: DataTypes.BOOLEAN,
      defaultValue: false,
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
    hooks: {
      beforeCreate: async (post) => {
        post.title_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          post.title
        );
      },
      beforeUpdate: async (post) => {
        post.title_non_accent = Converter.toLowerCaseNonAccentVietnamese(
          post.title
        );
      },
    },
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

(async () => {
  // await Post.sync({ force: true });
  // await Post.create({
  //   title: "Post 1",
  //   brief_description: "Post 1 brief description",
  //   created_by: 1,
  // });
  // await sequelize.models.users_posts_like.create({
  //   user_id: 1,
  //   post_id: 1,
  // });
  // await Post.findByPk(1, {
  //   attributes: {
  //     exclude: ["created_by"],
  //   },
  //   include: [
  //     {
  //       model: User,
  //       as: "liked_by",
  //       attributes: ["id", "email"],
  //       through: { attributes: [] },
  //     },
  //     {
  //       model: User,
  //       as: "author",
  //       attributes: ["id", "email"],
  //     },
  //   ],
  // }).then((post) => console.log(JSON.stringify(post, null, 2)));
  // await User.findByPk(1, {
  //   // attributes: {
  //   //   exclude: ["created_by"],
  //   // },
  //   include: [
  //     {
  //       model: Post,
  //       as: "liked_posts",
  //       attributes: {
  //         exclude: ["created_by"],
  //       },
  //       through: { attributes: [] },
  //     },
  //   ],
  // }).then((post) => console.log(JSON.stringify(post, null, 2)));
})();

module.exports = Post;

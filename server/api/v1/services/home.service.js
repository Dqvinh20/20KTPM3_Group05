const Post = require("../models/post.model");
const User = require("../models/user.model");

const queryAuthor = {
  association: "author",
  attributes: ["id", "email", "avatar", "user_name", "user_name_non_accent"],
};

const commonPost = (opts) => ({
  attributes: { exclude: ["created_by", "title_non_accent"] },
  where: { is_public: true },
  include: [{ ...queryAuthor }],
  ...opts,
});

const getPopularPosts = async ({ offset, limit }) => {
  const result = await Post.findAll(
    commonPost({
      order: [
        ["view_count", "DESC"],
        ["like_count", "DESC"],
      ],
      offset: offset ? offset : 0,
      limit,
    })
  );

  return result;
};

const countAllNewestPosts = async () => {
  const count = await Post.count({
    where: { is_public: true },
    order: [["createdAt", "DESC"]],
  });
  return count;
};

const getNewestPosts = async ({ offset, limit }) => {
  const result = await Post.findAll(
    commonPost({
      order: [["createdAt", "DESC"]],
      offset: offset ? offset : 0,
      limit,
    })
  );
  return result;
};

module.exports = {
  getPopularPosts,
  getNewestPosts,
  countAllNewestPosts,
};

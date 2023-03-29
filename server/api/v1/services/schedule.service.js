const { Op, json } = require("sequelize");
const dayjs = require("dayjs");

const Schedule = require("../models/schedule.model");
const Post = require("../models/post.model");
const SchedulesLocations = require("../models/schedule_location.model");
const Converter = require("../utils/converter");
const LocationService = require("../services/location.service");

const commonSchedule = (opts) => {
  return {
    attributes: {
      exclude: ["post_id"],
    },
    include: [
      {
        association: "locations",
        attributes: {
          exclude: ["place_id", "_search"],
        },
      },
    ],
    ...opts,
  };
};

const getAllLocationsInSchedule = async (schedule_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  return await schedule.getLocations();
};

const addLocation = async (schedule_id, location_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  const location = await LocationService.getLocationById(location_id);
  if (!location) {
    throw new Error("Location does not exist");
  } else if (await schedule.hasLocation(location_id)) {
    throw new Error("Location already exists in schedule");
  }

  const [[locationResult], scheduleResult] = await Promise.all([
    schedule.addLocation(location_id),
    schedule.increment("location_count", { by: 1 }),
  ]);

  locationResult.position = scheduleResult.location_count;
  await locationResult.save();

  return await schedule.getLocations({
    where: { id: location_id },
  });
};

const editLocationNote = async (schedule_id, location_id, note) => {
  const locationInSchedule = await SchedulesLocations.findOne({
    where: {
      schedule_id: schedule_id,
      location_id: location_id,
    },
  });

  if (!locationInSchedule) {
    throw new Error("Location does not exist in schedule");
  }

  locationInSchedule.note = note;
  await locationInSchedule.save();
  return locationInSchedule;
};

const removeLocation = async (schedule_id, location_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  let currPosition = await schedule.getLocations({
    where: { id: location_id },
    through: { attributes: ["position"] },
  });
  if (currPosition.length !== 0) {
    currPosition = currPosition[0].SchedulesLocations.position;
  }

  const result = await schedule.removeLocation(location_id);
  await schedule.decrement("location_count", { by: result });

  if (result === 1) {
    const nextLocations = await schedule.getLocations({
      through: {
        where: {
          position: {
            [Op.gt]: currPosition,
          },
        },
      },
    });

    await Promise.all(
      nextLocations.map(async (location) => {
        location.SchedulesLocations.position -= 1;
        await location.SchedulesLocations.save();
      })
    );
  }

  return result;
};

const getAllSchedulesInPost = async (post_id) => {
  const result = await Schedule.findAll(
    commonSchedule({
      where: {
        post_id: post_id,
      },
    })
  );
  return result;
};

const getScheduleById = async (id) => {
  return await Schedule.findByPk(id);
};

const removeAllSchedules = async (post_id) => {
  return await Schedule.destroy({ where: { post_id: post_id } });
};

const changeScheduleRange = async (post_id, start, end) => {
  try {
    const post = await Post.findByPk(post_id, {
      attributes: ["start_date", "end_date"],
    });

    const newEnd = new Date(end);
    const newStart = new Date(start);

    if (newStart > newEnd) {
      return;
    }
    await removeBetween(
      post_id,
      post.start_date,
      dayjs(newStart).subtract(1, "day").toDate()
    );
    await removeBetween(
      post_id,
      dayjs(newEnd).add(1, "day").toDate(),
      post.end_date
    );
    await addBetween(post_id, newStart, newEnd);
    await Post.update(
      {
        start_date: newStart,
        end_date: newEnd,
      },
      {
        where: {
          id: post_id,
        },
      }
    );
  } catch (e) {
    throw e;
  }
};

const addBetween = async (post_id, start, end) => {
  const schedules = [];
  const start_date = new Date(start);
  const end_date = new Date(end);

  const diffDate = end_date.getDate() - start_date.getDate();
  let current_date = new Date(start_date);

  for (let i = 0; i <= diffDate; i++) {
    const datePlan = new Date(current_date);
    await Schedule.findOne({
      where: {
        post_id,
        date: datePlan,
      },
    }).then((result) => {
      if (!result) {
        schedules.push({
          post_id: post_id,
          date: datePlan,
          title: Converter.toScheduleTitle(datePlan),
        });
      }
    });
    current_date.setDate(current_date.getDate() + 1);
  }

  return await Schedule.bulkCreate(schedules);
};

const removeBetween = async (post_id, start, end) => {
  return await Schedule.destroy({
    where: {
      [Op.and]: [
        { post_id },
        {
          date: {
            [Op.between]: [new Date(start), new Date(end)],
          },
        },
      ],
    },
  });
};

module.exports = {
  getAllLocationsInSchedule,
  editLocationNote,
  addLocation,
  removeLocation,
  getAllSchedulesInPost,
  getScheduleById,
  removeAllSchedules,
  changeScheduleRange,
  addBetween,
  model: Schedule,
};

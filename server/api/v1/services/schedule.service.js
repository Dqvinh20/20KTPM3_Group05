const { Op, json } = require("sequelize");
const dayjs = require("dayjs");

const Schedule = require("../models/schedule.model");
const Post = require("../models/post.model");
const SchedulesLocations = require("../models/schedule_location.model");
const Converter = require("../utils/converter");
const LocationService = require("../services/location.service");

const getAllLocations = async (schedule_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  return await schedule.getLocations();
};

const addLocation = async (schedule_id, location_id, newLocation) => {
  const schedule = await Schedule.findByPk(schedule_id);
  const location = await LocationService.getLocationById(location_id);
  if (!location) {
    await Promise.all([
      schedule.createLocation(newLocation),
      schedule.increment("location_count", { by: 1 }),
    ]);
  } else if (!(await schedule.hasLocation(location_id))) {
    await Promise.all([
      schedule.addLocation(location_id),
      schedule.increment("location_count", { by: 1 }),
    ]);
  }
  return schedule;
};

const addLocationNote = async (schedule_id, location_id, note) => {
  const locationInSchedule = await SchedulesLocations.findOne({
    where: {
      schedule_id: schedule_id,
      location_id: location_id,
    },
  });
  locationInSchedule.note = note;
  await locationInSchedule.save();
  return locationInSchedule;
};

const removeLocation = async (schedule_id, location_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  const result = await schedule.removeLocation(location_id);
  await schedule.decrement("location_count", { by: result });
  return schedule;
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
  getAllLocations,
  addLocationNote,
  addLocation,
  removeLocation,
  getScheduleById,
  removeAllSchedules,
  changeScheduleRange,
  addBetween,
};

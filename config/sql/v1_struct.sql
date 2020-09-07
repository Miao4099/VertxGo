/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : blog_pro

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 10/02/2020 14:46:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对应的产品id',
  `article_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章标题',
  `article_abstract` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章摘要',
  `abstract_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章摘要纯文本',
  `article_tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章标签，英文逗号分隔',
  `author_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章作者 id',
  `comment_count` int(11) NULL DEFAULT NULL COMMENT '文章评论计数',
  `review_count` int(11) NULL DEFAULT NULL COMMENT '文章浏览计数',
  `article_content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章正文，HTML base64编码',
  `article_permalink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章访问路径',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章是否置顶',
  `is_commentable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '	文章是否可评论',
  `review_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章浏览密码',
  `first_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章首图地址',
  `article_status` int(11) NULL DEFAULT NULL COMMENT '文章状态，已发布：1 ；草稿：2；禁用 3，0：是空',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '文章创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '文章更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blogs
-- ----------------------------
DROP TABLE IF EXISTS `blogs`;
CREATE TABLE `blogs`  (
  `blog_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对应的产品id',
  `blog_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章标题',
  `blog_abstract` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章摘要',
  `abstract_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章摘要纯文本',
  `blog_tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章标签，英文逗号分隔',
  `author_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章作者 id',
  `comment_count` int(11) NULL DEFAULT NULL COMMENT '文章评论计数',
  `review_count` int(11) NULL DEFAULT NULL COMMENT '文章浏览计数',
  `blog_content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章正文，HTML base64编码',
  `blog_permalink` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章访问路径',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章是否置顶',
  `is_commentable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '	文章是否可评论',
  `review_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章浏览密码',
  `first_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章首图地址',
  `blog_status` int(11) NULL DEFAULT NULL COMMENT '文章状态，已发布：0 ；草稿：1；禁用 2',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '文章创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '文章更新时间',
  PRIMARY KEY (`blog_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `blogs_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `comment_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论id',
  `comment_kind` int(2) NOT NULL COMMENT '评论类型：1：口感评测 2：评鉴报告',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品id',
  `milestone_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点id',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论人名字',
  `comment_state` int(2) NOT NULL COMMENT '状态：1.发布 2.草稿 3.审批不通过 4审批通过',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论人头像地址',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论人名称',
  `content_tiaosuo` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条索评论内容',
  `image_tiaosuo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条索图片',
  `content_tangse` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '汤色评论内容',
  `image_tangse` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '汤色图片',
  `content_xiangqi` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '香气评论内容',
  `image_xiangqi` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '香气图片',
  `content_ziwei` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滋味评论内容',
  `image_ziwei` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滋味图片',
  `content_yedi` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '叶底评论内容',
  `image_yedi` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '叶底图片',
  `summary` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '综合评测',
  `level` int(2) UNSIGNED ZEROFILL NULL DEFAULT 00 COMMENT '综合评测等级分',
  `video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT ' 视频',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '评论创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '评论更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for events
-- ----------------------------
DROP TABLE IF EXISTS `events`;
CREATE TABLE `events`  (
  `event_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '事件id',
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品id',
  `milestone_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '里程碑id',
  `event_kind` int(2) NULL DEFAULT NULL COMMENT '事件类型：1：图片 2：视频 3：文字',
  `event_brief` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件简述',
  `event_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件图片',
  `event_video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件视频',
  `event_date` date NULL DEFAULT NULL COMMENT '事件时间',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '事件创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '事件更新时间',
  PRIMARY KEY (`event_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hypertexts
-- ----------------------------
DROP TABLE IF EXISTS `hypertexts`;
CREATE TABLE `hypertexts`  (
  `hyper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对应的产品id',
  `hyper_kind` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图文类型，如首页图文、或者时间轴图文',
  `for_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体id，如时间轴图文，就是依赖milestone_id,首页就是首页id',
  `hyper_background` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '背景图片',
  `hyper_brief` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图文简介',
  `hyper_content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章正文，HTML base64编码',
  `hyper_video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图文视频链接，只有一个',
  `video_snapshot` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频截图',
  `video_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频状态，已发布：1 ；草稿：2；禁用 3',
  `hyper_state` int(2) NULL DEFAULT NULL COMMENT '图文是否发布，已发布：1 ；草稿：2；禁用 3',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '图文创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '图文更新时间',
  PRIMARY KEY (`hyper_id`) USING BTREE,
  UNIQUE INDEX `idx_brief`(`hyper_brief`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'log id',
  `req` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `rsp` varchar(8912) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回数据',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT 'log创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'log更新时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logs
-- ----------------------------
INSERT INTO `logs` VALUES ('MSG_USER_LIST', '{\"msg_id\":\"MSG_USER_LIST\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"page_index\":1,\"page_size\":50,\"filters\":[{\"fn\":\"name\",\"fv\":\"\",\"op\":\"like\"},{\"fn\":\"role\",\"fv\":\"\"}],\"sorter\":[{\"fn\":\"\",\"order\":\"-\"}]}}', '{\"numRows\":1,\"rows\":[{\"sex\":null,\"role\":\"admin\",\"avatar\":null,\"created_at\":null,\"province\":null,\"country\":null,\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"name\":\"admin\",\"city\":null}],\"pageSize\":50,\"pageIndex\":1,\"total\":1}', '2020-02-10 14:42:00', '2020-02-10 14:42:00');
INSERT INTO `logs` VALUES ('MSG_USER_ADD', '{\"msg_id\":\"MSG_USER_ADD\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"name\":\"admin2\",\"role\":\"admin\",\"sex\":\"1\",\"password\":\"123456\",\"password2\":\"123456\"}}', '{\"user_id\":\"e5ddbcb0f7f2c8b8bb05336a1efd900a\"}', '2020-02-10 14:42:21', '2020-02-10 14:42:21');
INSERT INTO `logs` VALUES ('MSG_USER_LIST', '{\"msg_id\":\"MSG_USER_LIST\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"page_index\":1,\"page_size\":50,\"filters\":[{\"fn\":\"name\",\"fv\":\"\",\"op\":\"like\"},{\"fn\":\"role\",\"fv\":\"\"}],\"sorter\":[{\"fn\":\"\",\"order\":\"-\"}]}}', '{\"numRows\":2,\"rows\":[{\"sex\":\"1\",\"role\":\"admin\",\"avatar\":\"https://api.shan-tea.com/admin/resource/admin.png\",\"created_at\":\"2020-02-10T06:42:21Z\",\"province\":null,\"country\":null,\"user_id\":\"e5ddbcb0f7f2c8b8bb05336a1efd900a\",\"name\":\"admin2\",\"city\":null},{\"sex\":null,\"role\":\"admin\",\"avatar\":null,\"created_at\":null,\"province\":null,\"country\":null,\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"name\":\"admin\",\"city\":null}],\"pageSize\":50,\"pageIndex\":1,\"total\":2}', '2020-02-10 14:42:21', '2020-02-10 14:42:21');
INSERT INTO `logs` VALUES ('MSG_USER_DEL', '{\"msg_id\":\"MSG_USER_DEL\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"ids\":[\"516a6a4a003b139e5a6619dfdc2bc0a7\"]}}', '{}', '2020-02-10 14:42:26', '2020-02-10 14:42:26');
INSERT INTO `logs` VALUES ('MSG_USER_LIST', '{\"msg_id\":\"MSG_USER_LIST\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"page_index\":1,\"page_size\":50,\"filters\":[{\"fn\":\"name\",\"fv\":\"\",\"op\":\"like\"},{\"fn\":\"role\",\"fv\":\"\"}],\"sorter\":[{\"fn\":\"\",\"order\":\"-\"}]}}', '{\"numRows\":1,\"rows\":[{\"sex\":\"1\",\"role\":\"admin\",\"avatar\":\"https://api.shan-tea.com/admin/resource/admin.png\",\"created_at\":\"2020-02-10T06:42:21Z\",\"province\":null,\"country\":null,\"user_id\":\"e5ddbcb0f7f2c8b8bb05336a1efd900a\",\"name\":\"admin2\",\"city\":null}],\"pageSize\":50,\"pageIndex\":1,\"total\":1}', '2020-02-10 14:42:26', '2020-02-10 14:42:26');
INSERT INTO `logs` VALUES ('MSG_USER_UPDATE', '{\"msg_id\":\"MSG_USER_UPDATE\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"sex\":\"1\",\"role\":\"admin\",\"avatar\":\"https://api.shan-tea.com/admin/resource/admin.png\",\"created_at\":\"2020-02-10T06:42:21Z\",\"province\":null,\"country\":null,\"user_id\":\"e5ddbcb0f7f2c8b8bb05336a1efd900a\",\"name\":\"admin01\",\"city\":null,\"password\":\"123456\",\"password2\":\"123456\"}}', '{}', '2020-02-10 14:42:54', '2020-02-10 14:42:54');
INSERT INTO `logs` VALUES ('MSG_USER_LIST', '{\"msg_id\":\"MSG_USER_LIST\",\"token\":\"3b51cb9c80389abfd8bec1ee6b9798b4\",\"user_id\":\"516a6a4a003b139e5a6619dfdc2bc0a7\",\"user_avatar\":\"\",\"user_name\":\"admin\",\"json\":{\"page_index\":1,\"page_size\":50,\"filters\":[{\"fn\":\"name\",\"fv\":\"\",\"op\":\"like\"},{\"fn\":\"role\",\"fv\":\"\"}],\"sorter\":[{\"fn\":\"\",\"order\":\"-\"}]}}', '{\"numRows\":1,\"rows\":[{\"sex\":\"1\",\"role\":\"admin\",\"avatar\":\"https://api.shan-tea.com/admin/resource/admin.png\",\"created_at\":\"2020-02-10T06:42:21Z\",\"province\":null,\"country\":null,\"user_id\":\"e5ddbcb0f7f2c8b8bb05336a1efd900a\",\"name\":\"admin01\",\"city\":null}],\"pageSize\":50,\"pageIndex\":1,\"total\":1}', '2020-02-10 14:42:54', '2020-02-10 14:42:54');

-- ----------------------------
-- Table structure for materials
-- ----------------------------
DROP TABLE IF EXISTS `materials`;
CREATE TABLE `materials`  (
  `material_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品id',
  `kind` int(2) NOT NULL COMMENT '素材类型：0：oss图片 1：oss视频 2：文字',
  `material_state` int(2) NULL DEFAULT NULL COMMENT '素材是否发布，已发布：1 ；草稿：2；禁用 3',
  `material_brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '素材简述',
  `material_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'home,contact,company 等类型',
  `oss_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'oss图片名',
  `oss_video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'oss视频名',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`material_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milestones
-- ----------------------------
DROP TABLE IF EXISTS `milestones`;
CREATE TABLE `milestones`  (
  `milestone_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应的产品id',
  `milestone_kind` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '里程碑的类型',
  `milestone_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '里程碑的名字',
  `milestone_order` int(255) NULL DEFAULT NULL COMMENT '里程碑序号',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '里程碑创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '里程碑更新时间',
  PRIMARY KEY (`milestone_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `news_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `news_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻都是跳转到公众号的',
  `news_state` int(2) NULL DEFAULT NULL COMMENT '新闻状态',
  `news_date` date NULL DEFAULT NULL COMMENT '新闻上线时间',
  `news_brief` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻简述',
  `news_content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新闻内容',
  `news_video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻视频',
  `news_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻图片',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '新闻创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '新闻更新时间',
  PRIMARY KEY (`news_id`) USING BTREE,
  UNIQUE INDEX `idx_brief`(`news_brief`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for points
-- ----------------------------
DROP TABLE IF EXISTS `points`;
CREATE TABLE `points`  (
  `point_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '觀點id',
  `report_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '觀點所屬的report',
  `point_kind` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '觀點類型',
  `point_data` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '根據point_kind存放不同内容，可以是評論文字，圖片地址',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT 'point创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'point更新时间',
  `point_order` int(11) NULL DEFAULT NULL COMMENT 'point在同一篇report中的排序',
  PRIMARY KEY (`point_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for prices
-- ----------------------------
DROP TABLE IF EXISTS `prices`;
CREATE TABLE `prices`  (
  `price_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应的产品id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '产品价格',
  `price_date` date NULL DEFAULT NULL COMMENT '价格的对应时间',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '价格创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '价格更新时间',
  PRIMARY KEY (`price_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_classes
-- ----------------------------
DROP TABLE IF EXISTS `product_classes`;
CREATE TABLE `product_classes`  (
  `class_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'classid',
  `class_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品系列名称',
  `class_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系列图片',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '产品系列创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '产品系列更新时间',
  PRIMARY KEY (`class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_milestones
-- ----------------------------
DROP TABLE IF EXISTS `product_milestones`;
CREATE TABLE `product_milestones`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'product',
  `milestone_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'milestone',
  `milestone_date` datetime(0) NOT NULL COMMENT 'milestone时间',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT 'map创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'map更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品id',
  `product_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `product_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品图片地址',
  `class_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品种类',
  `pre_sale` tinyint(1) NULL DEFAULT NULL COMMENT '是否是预售产品',
  `product_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品价格',
  `product_state` int(2) NULL DEFAULT NULL COMMENT '产品状态，已发布：0 ；草稿：1；禁用 2；发布：3',
  `market_date` date NULL DEFAULT NULL COMMENT '产品上市时间',
  `product_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品金句',
  `product_brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品简介',
  `tmall_link` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '天猫口令',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '产品创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '产品更新时间',
  PRIMARY KEY (`product_id`) USING BTREE,
  UNIQUE INDEX `idx_product_name`(`product_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shop_infos
-- ----------------------------
DROP TABLE IF EXISTS `shop_infos`;
CREATE TABLE `shop_infos`  (
  `info_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `shop_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `info_brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信息文字',
  `info_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信息图片',
  `info_video` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信息视频',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '信息创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '信息更新时间',
  PRIMARY KEY (`info_id`, `shop_id`) USING BTREE,
  INDEX `shop_id`(`shop_id`) USING BTREE,
  CONSTRAINT `shop_infos_ibfk_1` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shops
-- ----------------------------
DROP TABLE IF EXISTS `shops`;
CREATE TABLE `shops`  (
  `shop_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `shop_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商铺名称，其它日后扩展',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '商铺创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '商铺更新时间',
  PRIMARY KEY (`shop_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for structs
-- ----------------------------
DROP TABLE IF EXISTS `structs`;
CREATE TABLE `structs`  (
  `struct_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `struct_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结构名称',
  `hyper_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对应的素材',
  PRIMARY KEY (`struct_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for taste_reports
-- ----------------------------
DROP TABLE IF EXISTS `taste_reports`;
CREATE TABLE `taste_reports`  (
  `report_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '口感評測report_id',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '發佈用戶的id',
  `is_disable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT ' 是否被禁止',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT 'report创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'report更新时间',
  PRIMARY KEY (`report_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `user_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户链接',
  `user_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户角色',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `user_sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户性别',
  `user_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户类型',
  `user_country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户国家',
  `user_province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户省份',
  `user_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户城市',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '用户创建时间',
  `updated_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '用户更新时间',
  `openid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'openid',
  PRIMARY KEY (`user_id`, `user_name`) USING BTREE,
  UNIQUE INDEX `u_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('e5ddbcb0f7f2c8b8bb05336a1efd900a', 'admin', NULL, 'admin', 'https://api.shan-tea.com/admin/resource/admin.png', 'e10adc3949ba59abbe56e057f20f883e', '1', '0', NULL, NULL, NULL, '2020-02-10 14:42:21', '2020-02-10 14:43:20', NULL);

-- ----------------------------
-- Table structure for visits
-- ----------------------------
DROP TABLE IF EXISTS `visits`;
CREATE TABLE `visits`  (
  `visit_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `visit_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问数据名称',
  `visit_data` json NULL COMMENT '访问数据',
  PRIMARY KEY (`visit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

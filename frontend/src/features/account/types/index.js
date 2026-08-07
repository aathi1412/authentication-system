/**
 * @typedef {Object} User
 * @property {number} id
 * @property {string} name
 * @property {string} email
 * @property {string} phone
 * @property {string} bio
 * @property {string} [profileImage]
 */

/**
 * @typedef {Object} SecurityInfo
 * @property {boolean} emailVerified
 * @property {"USER"|"ADMIN"|"SUPER_ADMIN"} role
 * @property {boolean} accountLocked
 * @property {number} failedAttempts
 * @property {string} lastLogin  ISO date string
 * @property {string} createdAt  ISO date string
 */

/**
 * @typedef {"LOGIN"|"PASSWORD_CHANGE"|"PROFILE_UPDATE"|"EMAIL_VERIFIED"|"FAILED_LOGIN"|"PASSWORD_RESET"} ActivityType
 */

/**
 * @typedef {Object} ActivityLog
 * @property {number} id
 * @property {ActivityType} type
 * @property {string} title
 * @property {string} description
 * @property {string} createdAt  ISO date string
 */

export {};

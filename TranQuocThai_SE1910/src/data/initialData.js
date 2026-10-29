/**
 * Dữ liệu mẫu ban đầu cho Thể loại (Categories).
 * status: 1 = Active, 0 = Inactive.
 */
export const initialCategories = [
  { id: 1, name: 'Education', status: 1 },
  { id: 2, name: 'Technology', status: 1 },
  { id: 3, name: 'Campus Life', status: 0 },
]

/**
 * Dữ liệu mẫu ban đầu cho Tin tức (News Articles).
 * categoryId tham chiếu đến ID của initialCategories.
 */
export const initialNews = [
  {
    id: 1,
    title: 'New semester orientation schedule announced',
    content: 'The university has published the orientation schedule for new students.',
    categoryId: 1,
    createdBy: 'Admin',
    status: 1,
  },
  {
    id: 2,
    title: 'Technology club opens member registration',
    content: 'Students interested in software and emerging technology can register this week.',
    categoryId: 2,
    createdBy: 'Admin',
    status: 1,
  },
  {
    id: 3,
    title: 'Campus volunteer day',
    content: 'The planned volunteer event is waiting for final confirmation.',
    categoryId: 3,
    createdBy: 'Admin',
    status: 0,
  },
]

/**
 * Dữ liệu mẫu ban đầu cho Tài khoản người dùng (User Accounts).
 * role: 1 = Admin, 2 = Staff.
 */
export const initialUsers = [
  { id: 1, username: 'Admin', password: 'Admin', role: 1, status: 1 },
  { id: 2, username: 'staff01', password: 'staff01', role: 2, status: 1 },
  { id: 3, username: 'staff02', password: 'staff02', role: 2, status: 0 },
]

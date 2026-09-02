/** 与后端 VO 一一对应的类型，字段名保持和 JSON 一致。 */

/** common/response/ApiResponse.java */
export type ApiResponse<T> = {
  code: number;
  message: string;
  data: T;
};

/** vo/UserVo.java */
export type UserVo = {
  id: number;
  name: string;
  age: number | null;
  email: string;
};

/** vo/LoginVo.java */
export type LoginVo = {
  token: string;
  /** token 有效期，单位秒 */
  expiresIn: number;
  user: UserVo;
};

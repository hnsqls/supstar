declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseListLong_ = {
    code?: number;
    data?: number[];
    message?: string;
  };

  type BaseResponseLoginUserVo_ = {
    code?: number;
    data?: LoginUserVo;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type downloadMonthlyAttendanceUsingGETParams = {
    /** month */
    month: number;
    /** year */
    year: number;
  };

  type exportMonthlyAttendanceUsingGETParams = {
    /** month */
    month: number;
    /** year */
    year: number;
  };

  type LoginUserVo = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };
}

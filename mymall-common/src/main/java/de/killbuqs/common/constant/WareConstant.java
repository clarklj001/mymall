package de.killbuqs.common.constant;

public class WareConstant {

	public enum PurchaseStatusEnum {
		CREATED(0, "新建"), ASSIGNED(1, "已分配"), RECEIVED(2, "已领取"), FINISHED(3, "已完成"), HASERROR(4, "有异常");

		PurchaseStatusEnum(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		private Integer code;
		private String msg;

		public Integer getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}
	}
	
	public enum PurchaseDetailStatusEnum {
		CREATED(0, "新建"), ASSIGNED(1, "已分配"), BUYING(2, "正在采购"), FINISHED(3, "已完成"), HASERROR(4, "采购失败");
		
		PurchaseDetailStatusEnum(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}
		
		private Integer code;
		private String msg;
		
		public Integer getCode() {
			return code;
		}
		
		public String getMsg() {
			return msg;
		}
	}
}

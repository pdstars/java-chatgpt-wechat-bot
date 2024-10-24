package cn.zhouyafeng.itchat4j.beans;

/**
 * 为了适配源代码和新代码，做此单例
 */
public class GlobeInfo {

    private byte[] qrCode = new byte[]{};

    public byte[] getQrCode() {
        byte[] code = qrCode;
        qrCode = new byte[]{};
        return code;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }
}

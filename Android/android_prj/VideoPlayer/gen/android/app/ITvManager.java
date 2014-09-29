/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\android_prj\\VideoPlayer\\src\\android\\app\\ITvManager.aidl
 */
package android.app;
//import android.app.*;
/**
 * System private API for talking with the tv manager service.
 *
 * {@hide}
 */
public interface ITvManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.app.ITvManager
{
private static final java.lang.String DESCRIPTOR = "android.app.ITvManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.app.ITvManager interface,
 * generating a proxy if needed.
 */
public static android.app.ITvManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.app.ITvManager))) {
return ((android.app.ITvManager)iin);
}
return new android.app.ITvManager.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_stopRpcServer:
{
data.enforceInterface(DESCRIPTOR);
this.stopRpcServer();
reply.writeNoException();
return true;
}
case TRANSACTION_dumpPliMemoryUsage:
{
data.enforceInterface(DESCRIPTOR);
this.dumpPliMemoryUsage();
reply.writeNoException();
return true;
}
case TRANSACTION_suspendRpcServer:
{
data.enforceInterface(DESCRIPTOR);
this.suspendRpcServer();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeRpcServer:
{
data.enforceInterface(DESCRIPTOR);
this.resumeRpcServer();
reply.writeNoException();
return true;
}
case TRANSACTION_restoreSysDefSetting:
{
data.enforceInterface(DESCRIPTOR);
this.restoreSysDefSetting();
reply.writeNoException();
return true;
}
case TRANSACTION_setScreenSaverTiming:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScreenSaverTiming(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setMenuLanguage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setMenuLanguage(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSerialCode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setSerialCode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getScreenSaverTiming:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getScreenSaverTiming();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMenuLanguage:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getMenuLanguage();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSerialCode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getSerialCode();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startPerformanceLog:
{
data.enforceInterface(DESCRIPTOR);
this.startPerformanceLog();
reply.writeNoException();
return true;
}
case TRANSACTION_stopPerformanceLog:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.stopPerformanceLog(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_clearEeprom:
{
data.enforceInterface(DESCRIPTOR);
this.clearEeprom();
reply.writeNoException();
return true;
}
case TRANSACTION_readEepToFile:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.readEepToFile();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_writeFileToEep:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.writeFileToEep();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_upgradeSystem:
{
data.enforceInterface(DESCRIPTOR);
this.upgradeSystem();
reply.writeNoException();
return true;
}
case TRANSACTION_getCurAPK:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurAPK();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_tvAutoScanStart:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.tvAutoScanStart(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_tvAutoScanStop:
{
data.enforceInterface(DESCRIPTOR);
this.tvAutoScanStop();
reply.writeNoException();
return true;
}
case TRANSACTION_tvAutoScanComplete:
{
data.enforceInterface(DESCRIPTOR);
this.tvAutoScanComplete();
reply.writeNoException();
return true;
}
case TRANSACTION_tvSeekScanStart:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.tvSeekScanStart(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_tvSeekScanStop:
{
data.enforceInterface(DESCRIPTOR);
this.tvSeekScanStop();
reply.writeNoException();
return true;
}
case TRANSACTION_tvScanManualStart:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.tvScanManualStart(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_tvScanManualStop:
{
data.enforceInterface(DESCRIPTOR);
this.tvScanManualStop();
reply.writeNoException();
return true;
}
case TRANSACTION_tvScanManualComplete:
{
data.enforceInterface(DESCRIPTOR);
this.tvScanManualComplete();
reply.writeNoException();
return true;
}
case TRANSACTION_tvScanInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.tvScanInfo(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isTvScanning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isTvScanning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAtvSeqScanStartFreq:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAtvSeqScanStartFreq();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAtvSeqScanEndFreq:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAtvSeqScanEndFreq();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_saveChannel:
{
data.enforceInterface(DESCRIPTOR);
this.saveChannel();
reply.writeNoException();
return true;
}
case TRANSACTION_playNextChannel:
{
data.enforceInterface(DESCRIPTOR);
this.playNextChannel();
reply.writeNoException();
return true;
}
case TRANSACTION_playPrevChannel:
{
data.enforceInterface(DESCRIPTOR);
this.playPrevChannel();
reply.writeNoException();
return true;
}
case TRANSACTION_playFirstChannel:
{
data.enforceInterface(DESCRIPTOR);
this.playFirstChannel();
reply.writeNoException();
return true;
}
case TRANSACTION_playHistoryChannel:
{
data.enforceInterface(DESCRIPTOR);
this.playHistoryChannel();
reply.writeNoException();
return true;
}
case TRANSACTION_updateTvChannelList:
{
data.enforceInterface(DESCRIPTOR);
android.app.tv.ChannelFilter _arg0;
if ((0!=data.readInt())) {
_arg0 = android.app.tv.ChannelFilter.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateTvChannelList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurChannel:
{
data.enforceInterface(DESCRIPTOR);
android.app.tv.ChannelInfo _result = this.getCurChannel();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getChannelInfoByIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
android.app.tv.ChannelInfo _result = this.getChannelInfoByIndex(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getChannelCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getChannelCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sortChannel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.sortChannel(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playChannelByIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playChannelByIndex(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playChannelByNum:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playChannelByNum(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playChannel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.playChannel(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playChannelByLCN:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.playChannelByLCN(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playFirstChannelInFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.playFirstChannelInFreq(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playChannelByChnumFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.playChannelByChnumFreq(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playNumberChannel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
boolean _result = this.playNumberChannel(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_swapChannelByIdxEx:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _arg2;
_arg2 = (0!=data.readInt());
this.swapChannelByIdxEx(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_swapChannelByNumEx:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _arg2;
_arg2 = (0!=data.readInt());
this.swapChannelByNumEx(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_reloadLastPlayedSource:
{
data.enforceInterface(DESCRIPTOR);
this.reloadLastPlayedSource();
reply.writeNoException();
return true;
}
case TRANSACTION_setCurChannelSkipped:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setCurChannelSkipped(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setCurAtvSoundStd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setCurAtvSoundStd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_fineTuneCurFrequency:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.fineTuneCurFrequency(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setCurChAudioCompensation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setCurChAudioCompensation(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setSource:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setSource(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBootSource:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBootSource(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSourceAndDisplayWindow:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
boolean _result = this.setSourceAndDisplayWindow(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurChannelSkipped:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getCurChannelSkipped();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurAtvSoundStd:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurAtvSoundStd();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurChAudioCompensation:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurChAudioCompensation();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSourceList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getSourceList();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getSourceListCnt:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.getSourceListCnt(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurSourceType:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getCurSourceType();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getBootSource:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBootSource();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurTvType:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurTvType();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurLiveSource:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurLiveSource();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setBrightness:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBrightness(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setContrast:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setContrast(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSaturation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSaturation(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setHue:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setHue(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSharpness:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
this.setSharpness(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDNR:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDNR(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAspectRatio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAspectRatio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_saveAspectRatio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.saveAspectRatio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPictureMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPictureMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setCurAtvColorStd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setCurAtvColorStd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAvColorStd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAvColorStd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setVgaAutoAdjust:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setVgaAutoAdjust();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setVgaHPosition:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
boolean _result = this.setVgaHPosition(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setVgaVPosition:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
boolean _result = this.setVgaVPosition(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setVgaPhase:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
boolean _result = this.setVgaPhase(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setVgaClock:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
boolean _result = this.setVgaClock(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setMagicPicture:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setMagicPicture(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDCR:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDCR(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDCC:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setDCC(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setBacklight:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBacklight(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_set3dMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.set3dMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_set3dDeep:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.set3dDeep(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_set3dLRSwap:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.set3dLRSwap(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_set3dCvrt2D:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
boolean _result = this.set3dCvrt2D(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_set3dStrength:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.set3dStrength(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_set3dModeAndChangeRatio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _arg2;
_arg2 = data.readInt();
boolean _result = this.set3dModeAndChangeRatio(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getIs3d:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getIs3d();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setColorTempRGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempRGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempGGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempGGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempBGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempBGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempROffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempROffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempGOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempGOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setColorTempBOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setColorTempBOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDisplayWindow:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.setDisplayWindow(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_setDisplayWindowPositionChange:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.setDisplayWindowPositionChange(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_setPanelOn:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setPanelOn(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_scaler_ForceBg:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.scaler_ForceBg(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_scaler_4k2kOSD_ForceBg:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.scaler_4k2kOSD_ForceBg(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setOverScan:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.setOverScan(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_getBrightness:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBrightness();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getContrast:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getContrast();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSaturation:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSaturation();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHue:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getHue();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSharpness:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSharpness();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getColorTempLevel:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getColorTempLevel();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDNR:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDNR();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDCC:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getDCC();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPictureMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPictureMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurAtvColorStd:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurAtvColorStd();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurAtvColorStdNoAuto:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurAtvColorStdNoAuto();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAvColorStd:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAvColorStd();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAvColorStdNoAuto:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAvColorStdNoAuto();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getVgaHPosition:
{
data.enforceInterface(DESCRIPTOR);
char _result = this.getVgaHPosition();
reply.writeNoException();
reply.writeInt(((int)_result));
return true;
}
case TRANSACTION_getVgaVPosition:
{
data.enforceInterface(DESCRIPTOR);
char _result = this.getVgaVPosition();
reply.writeNoException();
reply.writeInt(((int)_result));
return true;
}
case TRANSACTION_getVgaPhase:
{
data.enforceInterface(DESCRIPTOR);
char _result = this.getVgaPhase();
reply.writeNoException();
reply.writeInt(((int)_result));
return true;
}
case TRANSACTION_getVgaClock:
{
data.enforceInterface(DESCRIPTOR);
char _result = this.getVgaClock();
reply.writeNoException();
reply.writeInt(((int)_result));
return true;
}
case TRANSACTION_getAspectRatio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getAspectRatio(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMagicPicture:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getMagicPicture();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDCR:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDCR();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDCRDemoDate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDCRDemoDate();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBacklight:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBacklight();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_get3dMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.get3dMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_get3dDeep:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.get3dDeep();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_get3dLRSwap:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.get3dLRSwap();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_get3dCvrt2D:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.get3dCvrt2D();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPanelOn:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getPanelOn();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getResolutionInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getResolutionInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_checkDviMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.checkDviMode();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getOverScan:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getOverScan();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setScalerAutoColorRGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorRGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColorGGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorGGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColorBGain:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorBGain(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColorROffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorROffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColorGOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorGOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColorBOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setScalerAutoColorBOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setVolume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setMute:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setMute(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setBalance:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBalance(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setBass:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBass(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setTreble:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setTreble(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setTrueSurround:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setTrueSurround(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setClarity:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setClarity(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setTrueBass:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setTrueBass(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSubWoof:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setSubWoof(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSubWoofVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSubWoofVolume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setEqualizerMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setEqualizerMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setEqualizer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setEqualizer(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setOnOffMusic:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setOnOffMusic(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioHdmiOutput:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAudioHdmiOutput(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setSurroundSound:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSurroundSound(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioSpdifOutput:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAudioSpdifOutput(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWallEffect:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setWallEffect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAudioMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioEffect:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setAudioEffect(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setKeyToneVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setKeyToneVolume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAudioChannelSwap:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAudioChannelSwap(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAutoVolume:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setAutoVolume(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAutoVolume:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getAutoVolume();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getVolume:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getVolume();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMute:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getMute();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getBalance:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBalance();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getBass:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBass();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getTreble:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getTreble();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getEqualizerMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getEqualizerMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getEqualizer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getEqualizer(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getTrueSurround:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getTrueSurround();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getClarity:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getClarity();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getTrueBass:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getTrueBass();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getSubWoof:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getSubWoof();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getSubWoofVolume:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSubWoofVolume();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getOnOffMusic:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getOnOffMusic();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getWallEffect:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getWallEffect();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAudioMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAudioMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAudioVolume:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getAudioVolume();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getKeyToneVolume:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getKeyToneVolume();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAudioChannelSwap:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAudioChannelSwap();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setWLanTmpProfileName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setWLanTmpProfileName(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileSSID:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setWLanTmpProfileSSID(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileWifiMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanTmpProfileWifiMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileWifiSecurity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.setWLanTmpProfileWifiSecurity(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileDhcpHostIp:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
this.setWLanTmpProfileDhcpHostIp(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileDhcpStartIp:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
this.setWLanTmpProfileDhcpStartIp(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileDhcpEndIp:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
this.setWLanTmpProfileDhcpEndIp(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanTmpProfileWepIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanTmpProfileWepIndex(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanProfileCopyToTmp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanProfileCopyToTmp(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanProfileCopyFromTmp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanProfileCopyFromTmp(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanIpAddr:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
this.setWLanIpAddr(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_setWLanProfileActiveIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanProfileActiveIndex(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getWLanProfileName:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanProfileName(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanProfileSSID:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanProfileSSID(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanProfileWifiMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getWLanProfileWifiMode(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanProfileWifiSecurity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getWLanProfileWifiSecurity(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanProfileDhcpHostIp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanProfileDhcpHostIp(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanProfileDhcpStartIp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanProfileDhcpStartIp(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanProfileDhcpEndIp:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanProfileDhcpEndIp(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanProfileWepIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getWLanProfileWepIndex(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanProfileActiveIndex:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getWLanProfileActiveIndex();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanProfileTotalNumber:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getWLanProfileTotalNumber();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanIpAddr:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanIpAddr(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanDHCPState:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getWLanDHCPState();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getWLanApListSize:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getWLanApListSize();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanApName:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getWLanApName(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getWLanApSecurity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getWLanApSecurity(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanApStrength:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getWLanApStrength(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setWLanWpsMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWLanWpsMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getWLanWpsMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getWLanWpsMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWLanPinCode:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getWLanPinCode();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_wLanConnectStart:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.wLanConnectStart(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_wLanConnectStop:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.wLanConnectStop(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_wLanConnectQueryState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.wLanConnectQueryState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_wLan0ActivateState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.wLan0ActivateState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_wiredLanDHCPStart:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.wiredLanDHCPStart();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_wiredLanDhcpStop:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.wiredLanDhcpStop(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_wiredLanDhcpQueryState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.wiredLanDhcpQueryState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWiredLanDhcpEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getWiredLanDhcpEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getWiredLanIpAddr:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
java.lang.String _result = this.getWiredLanIpAddr(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getMacAddressInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getMacAddressInfo(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getMacAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getMacAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getColorTempData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getColorTempData(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setColorTempData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setColorTempData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setScalerAutoColor:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setScalerAutoColor();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_resetColorTemp:
{
data.enforceInterface(DESCRIPTOR);
this.resetColorTemp();
reply.writeNoException();
return true;
}
case TRANSACTION_setWiredLanManualInit:
{
data.enforceInterface(DESCRIPTOR);
this.setWiredLanManualInit();
reply.writeNoException();
return true;
}
case TRANSACTION_setWiredLanManualIp:
{
data.enforceInterface(DESCRIPTOR);
this.setWiredLanManualIp();
reply.writeNoException();
return true;
}
case TRANSACTION_setWiredLanIpAddr:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
this.setWiredLanIpAddr(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_setMacAddress:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
char _arg5;
_arg5 = (char)data.readInt();
this.setMacAddress(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_startDecodeImage:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
long _result = this.startDecodeImage(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_decodeImage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.decodeImage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_decodeImageEx:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
this.decodeImageEx(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_getDecodeImageResult:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getDecodeImageResult();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_stopDecodeImage:
{
data.enforceInterface(DESCRIPTOR);
this.stopDecodeImage();
reply.writeNoException();
return true;
}
case TRANSACTION_zoomIn:
{
data.enforceInterface(DESCRIPTOR);
this.zoomIn();
reply.writeNoException();
return true;
}
case TRANSACTION_zoomOut:
{
data.enforceInterface(DESCRIPTOR);
this.zoomOut();
reply.writeNoException();
return true;
}
case TRANSACTION_leftRotate:
{
data.enforceInterface(DESCRIPTOR);
this.leftRotate();
reply.writeNoException();
return true;
}
case TRANSACTION_rightRotate:
{
data.enforceInterface(DESCRIPTOR);
this.rightRotate();
reply.writeNoException();
return true;
}
case TRANSACTION_upRotate:
{
data.enforceInterface(DESCRIPTOR);
this.upRotate();
reply.writeNoException();
return true;
}
case TRANSACTION_downRotate:
{
data.enforceInterface(DESCRIPTOR);
this.downRotate();
reply.writeNoException();
return true;
}
case TRANSACTION_onZoomMoveUp:
{
data.enforceInterface(DESCRIPTOR);
this.onZoomMoveUp();
reply.writeNoException();
return true;
}
case TRANSACTION_onZoomMoveDown:
{
data.enforceInterface(DESCRIPTOR);
this.onZoomMoveDown();
reply.writeNoException();
return true;
}
case TRANSACTION_onZoomMoveLeft:
{
data.enforceInterface(DESCRIPTOR);
this.onZoomMoveLeft();
reply.writeNoException();
return true;
}
case TRANSACTION_onZoomMoveRight:
{
data.enforceInterface(DESCRIPTOR);
this.onZoomMoveRight();
reply.writeNoException();
return true;
}
case TRANSACTION_enableQFHD:
{
data.enforceInterface(DESCRIPTOR);
this.enableQFHD();
reply.writeNoException();
return true;
}
case TRANSACTION_disableQFHD:
{
data.enforceInterface(DESCRIPTOR);
this.disableQFHD();
reply.writeNoException();
return true;
}
case TRANSACTION_setSuperResolutionMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setSuperResolutionMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurQuadFHDMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurQuadFHDMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setCurQuadFHDMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setCurQuadFHDMode(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setFunctionParser:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.setFunctionParser(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getFunctionParser:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _result = this.getFunctionParser(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setOverScanAndAdjustment:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
int _arg5;
_arg5 = data.readInt();
boolean _arg6;
_arg6 = (0!=data.readInt());
int _arg7;
_arg7 = data.readInt();
this.setOverScanAndAdjustment(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
return true;
}
case TRANSACTION_getOverScanAndAdjustment:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getOverScanAndAdjustment(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getNoSignalDisplayReady:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getNoSignalDisplayReady();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setEnableBroadcast:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setEnableBroadcast(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setVideoAreaOn:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
this.setVideoAreaOn(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_setVideoAreaOff:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setVideoAreaOff(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSystemVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getSystemVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBootcodeVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBootcodeVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getEepVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getEepVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCpuVersion:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCpuVersion();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getReleaseDate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getReleaseDate();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPanelName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPanelName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getUartMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getUartMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setUartMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setUartMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getEepPage:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getEepPage();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getEepOffset:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getEepOffset();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getEepData:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getEepData();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setEepPage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setEepPage(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setEepOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setEepOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setEepData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setEepData(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startWifi:
{
data.enforceInterface(DESCRIPTOR);
this.startWifi();
reply.writeNoException();
return true;
}
case TRANSACTION_stopWifi:
{
data.enforceInterface(DESCRIPTOR);
this.stopWifi();
reply.writeNoException();
return true;
}
case TRANSACTION_getWifiState:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getWifiState();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getNsta:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getNsta(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setNsta:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setNsta(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getPattern:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPattern();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPattern:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPattern(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_rebootSystem:
{
data.enforceInterface(DESCRIPTOR);
this.rebootSystem();
reply.writeNoException();
return true;
}
case TRANSACTION_setFacSingleKey:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setFacSingleKey(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getFacSingleKey:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getFacSingleKey();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setWarmMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setWarmMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_exitSkyworthFactorySet:
{
data.enforceInterface(DESCRIPTOR);
this.exitSkyworthFactorySet();
reply.writeNoException();
return true;
}
case TRANSACTION_setBusoffMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setBusoffMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getFacAutoScanGuide:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getFacAutoScanGuide();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setFacAutoScanGuide:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setFacAutoScanGuide(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getFacWarmMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getFacWarmMode();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setFacWarmMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setFacWarmMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDDREnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getDDREnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setDDREnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setDDREnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDDRPhaseShift:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getDDRPhaseShift();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setDDRPhaseShift:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setDDRPhaseShift(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDDRStep:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDDRStep();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDDRStep:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDDRStep(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDDRPeriod:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDDRPeriod();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDDRPeriod:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDDRPeriod(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDDROffset:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDDROffset();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDDROffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDDROffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getLVDSEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLVDSEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setLVDSEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSDclkRange:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLVDSDclkRange();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setLVDSDclkRange:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setLVDSDclkRange(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSDclkFMDIV:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLVDSDclkFMDIV();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setLVDSDclkFMDIV:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setLVDSDclkFMDIV(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSNewMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getLVDSNewMode();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLVDSNewMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setLVDSNewMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSPLLOffset:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLVDSPLLOffset();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setLVDSPLLOffset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setLVDSPLLOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSOnlyEvenOdd:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getLVDSOnlyEvenOdd();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLVDSOnlyEvenOdd:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setLVDSOnlyEvenOdd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSEvenOrOdd:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getLVDSEvenOrOdd();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLVDSEvenOrOdd:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setLVDSEvenOrOdd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLVDSDrivingCurrent:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLVDSDrivingCurrent();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setLVDSDrivingCurrent:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setLVDSDrivingCurrent(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getBARCODE1:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBARCODE1();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBARCODE2:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBARCODE2();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBARCODE3:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBARCODE3();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getBARCODE4:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getBARCODE4();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setBARCODE1:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
char _arg5;
_arg5 = (char)data.readInt();
char _arg6;
_arg6 = (char)data.readInt();
char _arg7;
_arg7 = (char)data.readInt();
this.setBARCODE1(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
return true;
}
case TRANSACTION_setBARCODE2:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
char _arg5;
_arg5 = (char)data.readInt();
char _arg6;
_arg6 = (char)data.readInt();
char _arg7;
_arg7 = (char)data.readInt();
this.setBARCODE2(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
return true;
}
case TRANSACTION_setBARCODE3:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
char _arg5;
_arg5 = (char)data.readInt();
char _arg6;
_arg6 = (char)data.readInt();
char _arg7;
_arg7 = (char)data.readInt();
this.setBARCODE3(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
return true;
}
case TRANSACTION_setBARCODE4:
{
data.enforceInterface(DESCRIPTOR);
char _arg0;
_arg0 = (char)data.readInt();
char _arg1;
_arg1 = (char)data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
char _arg3;
_arg3 = (char)data.readInt();
char _arg4;
_arg4 = (char)data.readInt();
char _arg5;
_arg5 = (char)data.readInt();
char _arg6;
_arg6 = (char)data.readInt();
char _arg7;
_arg7 = (char)data.readInt();
this.setBARCODE4(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
return true;
}
case TRANSACTION_gldcOsdShow:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.gldcOsdShow(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isKeyDown:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isKeyDown(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setInitialFlag:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setInitialFlag(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getInitialFlag:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getInitialFlag();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getDtvTime:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDtvTime();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setRTKIRMouse:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setRTKIRMouse(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getChannelNameList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
java.lang.String _result = this.getChannelNameList(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurrentProgramInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurrentProgramInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurrentProgramDescription:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurrentProgramDescription();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurrentProgramRating:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurrentProgramRating();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_hasCurrentProgramWithSubtitle:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.hasCurrentProgramWithSubtitle();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCurAtvSoundSelect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurAtvSoundSelect();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurrentAudioLang:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurrentAudioLang();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurInputInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurInputInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurrentSetting_tv:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getCurrentSetting_tv(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getChannelFreqCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getChannelFreqCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getChannelFreqByTableIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getChannelFreqByTableIndex(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getChannelchannelNumByTableIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getChannelchannelNumByTableIndex(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getChannelCountByFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getChannelCountByFreq(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurChannelIndex:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurChannelIndex();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getChannelListChannelCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getChannelListChannelCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getChannelDataList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _result = this.getChannelDataList(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_recoverVideoSize:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.recoverVideoSize();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getVideoSize:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getVideoSize();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setVideoSize:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.setVideoSize(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurDtvSoundSelectList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurDtvSoundSelectList();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurDtvSoundSelectCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurDtvSoundSelectCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurAtvSoundSelectList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurAtvSoundSelectList();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurAtvSoundSelectCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurAtvSoundSelectCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDisplayFreeze:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setDisplayFreeze(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setCaptionMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setCaptionMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setAnalogCaption:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setAnalogCaption(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDigitalCaption:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setDigitalCaption(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setChannelLockEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setChannelLockEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setChannelFav:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setChannelFav(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setChannelSkip:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setChannelSkip(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setChannelBlock:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setChannelBlock(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setChannelDel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setChannelDel(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getChannelFav:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getChannelFav(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getChannelSkip:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getChannelSkip(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getChannelBlock:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getChannelBlock(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_queryTvStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.queryTvStatus(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setHdmiAudioSource:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setHdmiAudioSource(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getIsContentLocked:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getIsContentLocked();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setSourceLockEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setSourceLockEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSourceLockStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getSourceLockStatus(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setSourceLockStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setSourceLockStatus(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getSourceLockStatusByIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getSourceLockStatusByIndex(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setSourceLockStatusByIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setSourceLockStatusByIndex(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_startRecordTs:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.startRecordTs(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopRecordTs:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.stopRecordTs();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getEpgData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.getEpgData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getEpgDataByLCN:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.getEpgDataByLCN(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_getEpgListEpgCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getEpgListEpgCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setTvTimeZone:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
this.setTvTimeZone(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getEpgDataList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _result = this.getEpgDataList(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setParentalLockEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setParentalLockEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getParentalLockEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getParentalLockEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setParentalLockPasswd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setParentalLockPasswd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getParentalLockPasswd:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getParentalLockPasswd();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setParentalLockRegion:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setParentalLockRegion(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getParentalLockRegion:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getParentalLockRegion();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setParentalRatingDvb:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setParentalRatingDvb(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getParentalRatingDvb:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getParentalRatingDvb();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPasswordReverify:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setPasswordReverify(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPasswordReverify:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getPasswordReverify();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setPasswordVerified:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPasswordVerified(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPasswordVerified:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.getPasswordVerified(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setPipEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setPipEnable(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getPipEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getPipEnable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setPipSubSource:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setPipSubSource(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getPipSource:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getPipSource(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_clearPipSourceIndicator:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.clearPipSourceIndicator(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPipSubMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPipSubMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPipSubMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPipSubMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPipSubPosition:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPipSubPosition(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPipSubPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPipSubPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPipSubDisplayWindow:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.setPipSubDisplayWindow(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlStart:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlStart();
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlStop:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlStop();
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlPause:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlPause();
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlResume:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlResume();
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlStartHttp:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlStartHttp();
reply.writeNoException();
return true;
}
case TRANSACTION_transcodeControlStopHttp:
{
data.enforceInterface(DESCRIPTOR);
this.transcodeControlStopHttp();
reply.writeNoException();
return true;
}
case TRANSACTION_getRecoveryCmd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getRecoveryCmd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setRecoveryCmd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setRecoveryCmd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerDivX:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.registerDivX();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isDeviceActivated:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDeviceActivated();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_deregisterDivX:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.deregisterDivX();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_enableSpectrumData:
{
data.enforceInterface(DESCRIPTOR);
this.enableSpectrumData();
reply.writeNoException();
return true;
}
case TRANSACTION_disableSpectrumData:
{
data.enforceInterface(DESCRIPTOR);
this.disableSpectrumData();
reply.writeNoException();
return true;
}
case TRANSACTION_getSpectrumInfo:
{
data.enforceInterface(DESCRIPTOR);
android.app.tv.SpectrumDataInfo _result = this.getSpectrumInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_enableGetMediaTotalTime:
{
data.enforceInterface(DESCRIPTOR);
this.enableGetMediaTotalTime();
reply.writeNoException();
return true;
}
case TRANSACTION_disableGetMediaTotalTime:
{
data.enforceInterface(DESCRIPTOR);
this.disableGetMediaTotalTime();
reply.writeNoException();
return true;
}
case TRANSACTION_getMediaTotalTime:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
long _result = this.getMediaTotalTime(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.app.ITvManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
//SystemCommandExecutor

public void stopRpcServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRpcServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void dumpPliMemoryUsage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dumpPliMemoryUsage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void suspendRpcServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_suspendRpcServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void resumeRpcServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeRpcServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void restoreSysDefSetting() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_restoreSysDefSetting, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScreenSaverTiming(int itiming) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(itiming);
mRemote.transact(Stub.TRANSACTION_setScreenSaverTiming, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setMenuLanguage(int iLanguage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iLanguage);
mRemote.transact(Stub.TRANSACTION_setMenuLanguage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSerialCode(java.lang.String strSerCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strSerCode);
mRemote.transact(Stub.TRANSACTION_setSerialCode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getScreenSaverTiming() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getScreenSaverTiming, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getMenuLanguage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMenuLanguage, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getSerialCode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSerialCode, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void startPerformanceLog() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startPerformanceLog, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopPerformanceLog(java.lang.String strFilePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strFilePath);
mRemote.transact(Stub.TRANSACTION_stopPerformanceLog, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void clearEeprom() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_clearEeprom, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean readEepToFile() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_readEepToFile, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean writeFileToEep() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_writeFileToEep, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void upgradeSystem() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_upgradeSystem, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getCurAPK() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAPK, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//TvChannelApiExecutor

public boolean tvAutoScanStart(int tvType, boolean bUpdateScan) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(tvType);
_data.writeInt(((bUpdateScan)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_tvAutoScanStart, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void tvAutoScanStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tvAutoScanStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void tvAutoScanComplete() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tvAutoScanComplete, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean tvSeekScanStart(boolean bSeekForward) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bSeekForward)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_tvSeekScanStart, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void tvSeekScanStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tvSeekScanStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean tvScanManualStart(int iFreq, int iBandWidth, int iPhyChNum) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iFreq);
_data.writeInt(iBandWidth);
_data.writeInt(iPhyChNum);
mRemote.transact(Stub.TRANSACTION_tvScanManualStart, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void tvScanManualStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tvScanManualStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void tvScanManualComplete() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_tvScanManualComplete, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int tvScanInfo(int infoId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(infoId);
mRemote.transact(Stub.TRANSACTION_tvScanInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isTvScanning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isTvScanning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAtvSeqScanStartFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAtvSeqScanStartFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAtvSeqScanEndFreq() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAtvSeqScanEndFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void saveChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_saveChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playNextChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playNextChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playPrevChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playPrevChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playFirstChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playFirstChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playHistoryChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playHistoryChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void updateTvChannelList(android.app.tv.ChannelFilter filter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((filter!=null)) {
_data.writeInt(1);
filter.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateTvChannelList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public android.app.tv.ChannelInfo getCurChannel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.app.tv.ChannelInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurChannel, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.app.tv.ChannelInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public android.app.tv.ChannelInfo getChannelInfoByIndex(int iIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.app.tv.ChannelInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iIndex);
mRemote.transact(Stub.TRANSACTION_getChannelInfoByIndex, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.app.tv.ChannelInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getChannelCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean sortChannel(int policy) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(policy);
mRemote.transact(Stub.TRANSACTION_sortChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void playChannelByIndex(int iIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iIndex);
mRemote.transact(Stub.TRANSACTION_playChannelByIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playChannelByNum(int iNum) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iNum);
mRemote.transact(Stub.TRANSACTION_playChannelByNum, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean playChannel(int iIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iIndex);
mRemote.transact(Stub.TRANSACTION_playChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean playChannelByLCN(int iLcnNum) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iLcnNum);
mRemote.transact(Stub.TRANSACTION_playChannelByLCN, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean playFirstChannelInFreq(int iFreq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iFreq);
mRemote.transact(Stub.TRANSACTION_playFirstChannelInFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean playChannelByChnumFreq(int iSysChNum, int iFreq, java.lang.String tvSystem) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iSysChNum);
_data.writeInt(iFreq);
_data.writeString(tvSystem);
mRemote.transact(Stub.TRANSACTION_playChannelByChnumFreq, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean playNumberChannel(int majorNum, int minorNum, boolean isAudioFocus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(majorNum);
_data.writeInt(minorNum);
_data.writeInt(((isAudioFocus)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_playNumberChannel, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void swapChannelByIdxEx(int iChIdx, boolean bSwapChNum, boolean bPlayAfterSwap) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iChIdx);
_data.writeInt(((bSwapChNum)?(1):(0)));
_data.writeInt(((bPlayAfterSwap)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_swapChannelByIdxEx, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void swapChannelByNumEx(int iChNum, boolean bSwapChNum, boolean bPlayAfterSwap) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iChNum);
_data.writeInt(((bSwapChNum)?(1):(0)));
_data.writeInt(((bPlayAfterSwap)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_swapChannelByNumEx, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void reloadLastPlayedSource() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reloadLastPlayedSource, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setCurChannelSkipped(boolean bSkip) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bSkip)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setCurChannelSkipped, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setCurAtvSoundStd(int soundSystemId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(soundSystemId);
mRemote.transact(Stub.TRANSACTION_setCurAtvSoundStd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void fineTuneCurFrequency(int iOffset, boolean bPerminant) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iOffset);
_data.writeInt(((bPerminant)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_fineTuneCurFrequency, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setCurChAudioCompensation(int iValue, boolean bApply) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
_data.writeInt(((bApply)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setCurChAudioCompensation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setSource(int iIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iIndex);
mRemote.transact(Stub.TRANSACTION_setSource, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setBootSource(int sourceOpt) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sourceOpt);
mRemote.transact(Stub.TRANSACTION_setBootSource, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setSourceAndDisplayWindow(int src, int x, int y, int width, int height) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(src);
_data.writeInt(x);
_data.writeInt(y);
_data.writeInt(width);
_data.writeInt(height);
mRemote.transact(Stub.TRANSACTION_setSourceAndDisplayWindow, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getCurChannelSkipped() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurChannelSkipped, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurAtvSoundStd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvSoundStd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurChAudioCompensation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurChAudioCompensation, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getSourceList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSourceList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getSourceListCnt(boolean bWithoutPlayback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bWithoutPlayback)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getSourceListCnt, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long getCurSourceType() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurSourceType, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBootSource() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBootSource, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurTvType() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurTvType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurLiveSource() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurLiveSource, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//TvDisplaySetupApiExecutor

public void setBrightness(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setBrightness, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setContrast(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setContrast, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSaturation(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setSaturation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setHue(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setHue, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSharpness(boolean iApply, int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((iApply)?(1):(0)));
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setSharpness, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempMode(int level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(level);
mRemote.transact(Stub.TRANSACTION_setColorTempMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDNR(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setDNR, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAspectRatio(int ratio) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ratio);
mRemote.transact(Stub.TRANSACTION_setAspectRatio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void saveAspectRatio(int ratio) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ratio);
mRemote.transact(Stub.TRANSACTION_saveAspectRatio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setPictureMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setPictureMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setCurAtvColorStd(int colorStd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(colorStd);
mRemote.transact(Stub.TRANSACTION_setCurAtvColorStd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAvColorStd(int colorStd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(colorStd);
mRemote.transact(Stub.TRANSACTION_setAvColorStd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setVgaAutoAdjust() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setVgaAutoAdjust, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setVgaHPosition(char ucPosition) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ucPosition));
mRemote.transact(Stub.TRANSACTION_setVgaHPosition, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setVgaVPosition(char ucPosition) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ucPosition));
mRemote.transact(Stub.TRANSACTION_setVgaVPosition, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setVgaPhase(char ucValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ucValue));
mRemote.transact(Stub.TRANSACTION_setVgaPhase, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setVgaClock(char ucValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ucValue));
mRemote.transact(Stub.TRANSACTION_setVgaClock, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setMagicPicture(int magicPic) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(magicPic);
mRemote.transact(Stub.TRANSACTION_setMagicPicture, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDCR(int iDCR) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iDCR);
mRemote.transact(Stub.TRANSACTION_setDCR, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDCC(boolean iDccOn, boolean iIsApply) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((iDccOn)?(1):(0)));
_data.writeInt(((iIsApply)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDCC, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBacklight(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setBacklight, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void set3dMode(int imode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(imode);
mRemote.transact(Stub.TRANSACTION_set3dMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void set3dDeep(int imode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(imode);
mRemote.transact(Stub.TRANSACTION_set3dDeep, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void set3dLRSwap(boolean bOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_set3dLRSwap, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean set3dCvrt2D(boolean bOn, int iFrameFlag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
_data.writeInt(iFrameFlag);
mRemote.transact(Stub.TRANSACTION_set3dCvrt2D, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void set3dStrength(int iStrength) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iStrength);
mRemote.transact(Stub.TRANSACTION_set3dStrength, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean set3dModeAndChangeRatio(int iMode, boolean bMute, int iType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iMode);
_data.writeInt(((bMute)?(1):(0)));
_data.writeInt(iType);
mRemote.transact(Stub.TRANSACTION_set3dModeAndChangeRatio, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getIs3d() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIs3d, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setColorTempRGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempRGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempGGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempGGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempBGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempBGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempROffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempROffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempGOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempGOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setColorTempBOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempBOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDisplayWindow(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iX);
_data.writeInt(iY);
_data.writeInt(iWidth);
_data.writeInt(iHeight);
mRemote.transact(Stub.TRANSACTION_setDisplayWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDisplayWindowPositionChange(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iX);
_data.writeInt(iY);
_data.writeInt(iWidth);
_data.writeInt(iHeight);
mRemote.transact(Stub.TRANSACTION_setDisplayWindowPositionChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setPanelOn(boolean bOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setPanelOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void scaler_ForceBg(boolean bOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_scaler_ForceBg, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void scaler_4k2kOSD_ForceBg(boolean bOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_scaler_4k2kOSD_ForceBg, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setOverScan(int h_start, int h_width, int v_start, int v_length) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(h_start);
_data.writeInt(h_width);
_data.writeInt(v_start);
_data.writeInt(v_length);
mRemote.transact(Stub.TRANSACTION_setOverScan, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getBrightness() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBrightness, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getContrast() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getContrast, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getSaturation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSaturation, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getHue() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getHue, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getSharpness() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSharpness, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getColorTempLevel() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getColorTempLevel, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDNR() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDNR, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getDCC() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDCC, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getPictureMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPictureMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurAtvColorStd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvColorStd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurAtvColorStdNoAuto() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvColorStdNoAuto, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAvColorStd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAvColorStd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAvColorStdNoAuto() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAvColorStdNoAuto, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public char getVgaHPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
char _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVgaHPosition, _data, _reply, 0);
_reply.readException();
_result = (char)_reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public char getVgaVPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
char _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVgaVPosition, _data, _reply, 0);
_reply.readException();
_result = (char)_reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public char getVgaPhase() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
char _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVgaPhase, _data, _reply, 0);
_reply.readException();
_result = (char)_reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public char getVgaClock() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
char _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVgaClock, _data, _reply, 0);
_reply.readException();
_result = (char)_reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAspectRatio(int iSourceOption) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iSourceOption);
mRemote.transact(Stub.TRANSACTION_getAspectRatio, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getMagicPicture() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMagicPicture, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDCR() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDCR, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getDCRDemoDate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDCRDemoDate, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBacklight() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBacklight, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int get3dMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_get3dMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int get3dDeep() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_get3dDeep, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean get3dLRSwap() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_get3dLRSwap, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean get3dCvrt2D() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_get3dCvrt2D, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getPanelOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPanelOn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getResolutionInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getResolutionInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean checkDviMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_checkDviMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getOverScan() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getOverScan, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//TvFactoryApiExecutor

public void setScalerAutoColorRGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorRGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScalerAutoColorGGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorGGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScalerAutoColorBGain(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorBGain, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScalerAutoColorROffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorROffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScalerAutoColorGOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorGOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setScalerAutoColorBOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColorBOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//TvSoundSetupApiExecutor

public void setVolume(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setMute(boolean mute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mute)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setMute, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBalance(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setBalance, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBass(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setBass, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setTreble(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setTreble, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setTrueSurround(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setTrueSurround, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setClarity(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setClarity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setTrueBass(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setTrueBass, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSubWoof(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSubWoof, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSubWoofVolume(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setSubWoofVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setEqualizerMode(int iMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iMode);
mRemote.transact(Stub.TRANSACTION_setEqualizerMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setEqualizer(int iFreq, int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iFreq);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setEqualizer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setOnOffMusic(boolean bOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setOnOffMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAudioHdmiOutput(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setAudioHdmiOutput, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSurroundSound(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setSurroundSound, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAudioSpdifOutput(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setAudioSpdifOutput, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWallEffect(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setWallEffect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAudioMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setAudioMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAudioEffect(int audioEffect, int param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(audioEffect);
_data.writeInt(param);
mRemote.transact(Stub.TRANSACTION_setAudioEffect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setKeyToneVolume(int iVol) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iVol);
mRemote.transact(Stub.TRANSACTION_setKeyToneVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAudioChannelSwap(int sel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sel);
mRemote.transact(Stub.TRANSACTION_setAudioChannelSwap, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setAutoVolume(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setAutoVolume, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getAutoVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAutoVolume, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVolume, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getMute() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMute, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBalance() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBalance, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBass() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBass, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getTreble() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTreble, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getEqualizerMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEqualizerMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getEqualizer(int iFreq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iFreq);
mRemote.transact(Stub.TRANSACTION_getEqualizer, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getTrueSurround() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTrueSurround, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getClarity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getClarity, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getTrueBass() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTrueBass, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getSubWoof() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSubWoof, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getSubWoofVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSubWoofVolume, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getOnOffMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getOnOffMusic, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getWallEffect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWallEffect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAudioMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getAudioVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioVolume, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getKeyToneVolume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getKeyToneVolume, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAudioChannelSwap() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioChannelSwap, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//Invoke WLanSetupApiExecutor functions

public void setWLanTmpProfileName(java.lang.String pStrName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pStrName);
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileSSID(java.lang.String pStrSSID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pStrSSID);
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileSSID, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileWifiMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileWifiMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileWifiSecurity(int security, java.lang.String pStrKey) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(security);
_data.writeString(pStrKey);
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileWifiSecurity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileDhcpHostIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ip1));
_data.writeInt(((int)ip2));
_data.writeInt(((int)ip3));
_data.writeInt(((int)ip4));
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileDhcpHostIp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileDhcpStartIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ip1));
_data.writeInt(((int)ip2));
_data.writeInt(((int)ip3));
_data.writeInt(((int)ip4));
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileDhcpStartIp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileDhcpEndIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)ip1));
_data.writeInt(((int)ip2));
_data.writeInt(((int)ip3));
_data.writeInt(((int)ip4));
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileDhcpEndIp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanTmpProfileWepIndex(int iIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iIndex);
mRemote.transact(Stub.TRANSACTION_setWLanTmpProfileWepIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanProfileCopyToTmp(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_setWLanProfileCopyToTmp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanProfileCopyFromTmp(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_setWLanProfileCopyFromTmp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
_data.writeInt(((int)ip1));
_data.writeInt(((int)ip2));
_data.writeInt(((int)ip3));
_data.writeInt(((int)ip4));
mRemote.transact(Stub.TRANSACTION_setWLanIpAddr, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWLanProfileActiveIndex(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_setWLanProfileActiveIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getWLanProfileName(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanProfileSSID(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileSSID, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanProfileWifiMode(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileWifiMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanProfileWifiSecurity(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileWifiSecurity, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanProfileDhcpHostIp(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileDhcpHostIp, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanProfileDhcpStartIp(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileDhcpStartIp, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanProfileDhcpEndIp(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileDhcpEndIp, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanProfileWepIndex(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_getWLanProfileWepIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanProfileActiveIndex() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanProfileActiveIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanProfileTotalNumber() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanProfileTotalNumber, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanIpAddr(int netType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
mRemote.transact(Stub.TRANSACTION_getWLanIpAddr, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getWLanDHCPState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanDHCPState, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanApListSize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanApListSize, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanApName(int iApIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iApIndex);
mRemote.transact(Stub.TRANSACTION_getWLanApName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanApSecurity(int iApIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iApIndex);
mRemote.transact(Stub.TRANSACTION_getWLanApSecurity, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getWLanApStrength(int iApIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iApIndex);
mRemote.transact(Stub.TRANSACTION_getWLanApStrength, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setWLanWpsMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setWLanWpsMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getWLanWpsMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanWpsMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWLanPinCode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWLanPinCode, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void wLanConnectStart(int iProfileIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iProfileIndex);
mRemote.transact(Stub.TRANSACTION_wLanConnectStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void wLanConnectStop(boolean bForce) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bForce)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_wLanConnectStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int wLanConnectQueryState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_wLanConnectQueryState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int wLan0ActivateState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_wLan0ActivateState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//LanSetupApiExecutor

public boolean wiredLanDHCPStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_wiredLanDHCPStart, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void wiredLanDhcpStop(boolean bForceStop) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bForceStop)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_wiredLanDhcpStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int wiredLanDhcpQueryState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_wiredLanDhcpQueryState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getWiredLanDhcpEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWiredLanDhcpEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getWiredLanIpAddr(int netType, boolean bFromDatabase) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
_data.writeInt(((bFromDatabase)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getWiredLanIpAddr, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getMacAddressInfo(int iNetInterface) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iNetInterface);
mRemote.transact(Stub.TRANSACTION_getMacAddressInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getMacAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMacAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//tc_tan

public int getColorTempData(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getColorTempData, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setColorTempData(int index, int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setColorTempData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean setScalerAutoColor() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setScalerAutoColor, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void resetColorTemp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resetColorTemp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWiredLanManualInit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setWiredLanManualInit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWiredLanManualIp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setWiredLanManualIp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setWiredLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
_data.writeInt(((int)ip1));
_data.writeInt(((int)ip2));
_data.writeInt(((int)ip3));
_data.writeInt(((int)ip4));
mRemote.transact(Stub.TRANSACTION_setWiredLanIpAddr, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setMacAddress(char mac1, char mac2, char mac3, char mac4, char mac5, char mac6) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)mac1));
_data.writeInt(((int)mac2));
_data.writeInt(((int)mac3));
_data.writeInt(((int)mac4));
_data.writeInt(((int)mac5));
_data.writeInt(((int)mac6));
mRemote.transact(Stub.TRANSACTION_setMacAddress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//ImageDecoderApiExecutor

public long startDecodeImage(boolean bBackupHttpFileSource) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bBackupHttpFileSource)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_startDecodeImage, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void decodeImage(java.lang.String pFilePath, int transitionType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pFilePath);
_data.writeInt(transitionType);
mRemote.transact(Stub.TRANSACTION_decodeImage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void decodeImageEx(java.lang.String pFilePath, int transitionType, boolean bUpnpFile) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pFilePath);
_data.writeInt(transitionType);
_data.writeInt(((bUpnpFile)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_decodeImageEx, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public long getDecodeImageResult() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDecodeImageResult, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void stopDecodeImage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopDecodeImage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void zoomIn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_zoomIn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void zoomOut() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_zoomOut, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void leftRotate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_leftRotate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void rightRotate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_rightRotate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void upRotate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_upRotate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void downRotate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_downRotate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onZoomMoveUp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onZoomMoveUp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onZoomMoveDown() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onZoomMoveDown, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onZoomMoveLeft() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onZoomMoveLeft, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onZoomMoveRight() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onZoomMoveRight, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void enableQFHD() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableQFHD, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//Request by FengWen that control Qual-FHD enable/disable

public void disableQFHD() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disableQFHD, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSuperResolutionMode(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSuperResolutionMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getCurQuadFHDMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurQuadFHDMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setCurQuadFHDMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setCurQuadFHDMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setFunctionParser(int paramcounter, java.lang.String param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(paramcounter);
_data.writeString(param);
mRemote.transact(Stub.TRANSACTION_setFunctionParser, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getFunctionParser(int paramcounter, java.lang.String param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(paramcounter);
_data.writeString(param);
mRemote.transact(Stub.TRANSACTION_getFunctionParser, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setOverScanAndAdjustment(int h_ratio, int v_ratio, int h_start, int h_width, int v_start, int v_length, boolean applyalltiming, int customer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(h_ratio);
_data.writeInt(v_ratio);
_data.writeInt(h_start);
_data.writeInt(h_width);
_data.writeInt(v_start);
_data.writeInt(v_length);
_data.writeInt(((applyalltiming)?(1):(0)));
_data.writeInt(customer);
mRemote.transact(Stub.TRANSACTION_setOverScanAndAdjustment, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getOverScanAndAdjustment(int customer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(customer);
mRemote.transact(Stub.TRANSACTION_getOverScanAndAdjustment, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//NoSignal DisplayReady

public boolean getNoSignalDisplayReady() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNoSignalDisplayReady, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setEnableBroadcast(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setEnableBroadcast, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//Set interesting area of video

public void setVideoAreaOn(int x, int y, int w, int h, int plane) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(x);
_data.writeInt(y);
_data.writeInt(w);
_data.writeInt(h);
_data.writeInt(plane);
mRemote.transact(Stub.TRANSACTION_setVideoAreaOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setVideoAreaOff(int plane) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(plane);
mRemote.transact(Stub.TRANSACTION_setVideoAreaOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//Test class get set
//IcData getSetClass(IcData classData);	
//factory menu api

public java.lang.String getSystemVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSystemVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getBootcodeVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBootcodeVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getEepVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEepVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCpuVersion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCpuVersion, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getReleaseDate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getReleaseDate, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getPanelName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPanelName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getUartMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUartMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setUartMode(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setUartMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getEepPage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEepPage, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getEepOffset() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEepOffset, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getEepData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEepData, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setEepPage(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setEepPage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setEepOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setEepOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setEepData(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setEepData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void startWifi() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startWifi, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopWifi() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopWifi, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getWifiState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getWifiState, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getNsta(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_getNsta, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setNsta(int type, int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setNsta, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getPattern() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPattern, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPattern(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setPattern, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void rebootSystem() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_rebootSystem, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setFacSingleKey(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setFacSingleKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getFacSingleKey() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFacSingleKey, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setWarmMode(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setWarmMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void exitSkyworthFactorySet() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exitSkyworthFactorySet, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBusoffMode(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBusoffMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getFacAutoScanGuide() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFacAutoScanGuide, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setFacAutoScanGuide(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setFacAutoScanGuide, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getFacWarmMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFacWarmMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setFacWarmMode(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setFacWarmMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getDDREnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDDREnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setDDREnable(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDDREnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getDDRPhaseShift() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDDRPhaseShift, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setDDRPhaseShift(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDDRPhaseShift, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getDDRStep() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDDRStep, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setDDRStep(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setDDRStep, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getDDRPeriod() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDDRPeriod, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setDDRPeriod(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setDDRPeriod, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getDDROffset() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDDROffset, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setDDROffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setDDROffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getLVDSEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSEnable(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLVDSEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLVDSDclkRange() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSDclkRange, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSDclkRange(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setLVDSDclkRange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLVDSDclkFMDIV() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSDclkFMDIV, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSDclkFMDIV(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setLVDSDclkFMDIV, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getLVDSNewMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSNewMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSNewMode(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLVDSNewMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLVDSPLLOffset() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSPLLOffset, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSPLLOffset(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setLVDSPLLOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getLVDSOnlyEvenOdd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSOnlyEvenOdd, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSOnlyEvenOdd(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLVDSOnlyEvenOdd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getLVDSEvenOrOdd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSEvenOrOdd, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSEvenOrOdd(boolean mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((mode)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLVDSEvenOrOdd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLVDSDrivingCurrent() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLVDSDrivingCurrent, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLVDSDrivingCurrent(int iValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iValue);
mRemote.transact(Stub.TRANSACTION_setLVDSDrivingCurrent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getBARCODE1() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBARCODE1, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getBARCODE2() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBARCODE2, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getBARCODE3() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBARCODE3, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getBARCODE4() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBARCODE4, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setBARCODE1(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)bar1));
_data.writeInt(((int)bar2));
_data.writeInt(((int)bar3));
_data.writeInt(((int)bar4));
_data.writeInt(((int)bar5));
_data.writeInt(((int)bar6));
_data.writeInt(((int)bar7));
_data.writeInt(((int)bar8));
mRemote.transact(Stub.TRANSACTION_setBARCODE1, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBARCODE2(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)bar1));
_data.writeInt(((int)bar2));
_data.writeInt(((int)bar3));
_data.writeInt(((int)bar4));
_data.writeInt(((int)bar5));
_data.writeInt(((int)bar6));
_data.writeInt(((int)bar7));
_data.writeInt(((int)bar8));
mRemote.transact(Stub.TRANSACTION_setBARCODE2, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBARCODE3(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)bar1));
_data.writeInt(((int)bar2));
_data.writeInt(((int)bar3));
_data.writeInt(((int)bar4));
_data.writeInt(((int)bar5));
_data.writeInt(((int)bar6));
_data.writeInt(((int)bar7));
_data.writeInt(((int)bar8));
mRemote.transact(Stub.TRANSACTION_setBARCODE3, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setBARCODE4(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((int)bar1));
_data.writeInt(((int)bar2));
_data.writeInt(((int)bar3));
_data.writeInt(((int)bar4));
_data.writeInt(((int)bar5));
_data.writeInt(((int)bar6));
_data.writeInt(((int)bar7));
_data.writeInt(((int)bar8));
mRemote.transact(Stub.TRANSACTION_setBARCODE4, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean gldcOsdShow(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_gldcOsdShow, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isKeyDown(int key) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(key);
mRemote.transact(Stub.TRANSACTION_isKeyDown, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setInitialFlag(boolean bInitial) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bInitial)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setInitialFlag, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getInitialFlag() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getInitialFlag, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDtvTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDtvTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setRTKIRMouse(boolean setting) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((setting)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setRTKIRMouse, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getChannelNameList(int iStartIdx, int iContentLen, boolean bFilter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iStartIdx);
_data.writeInt(iContentLen);
_data.writeInt(((bFilter)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getChannelNameList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentProgramInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentProgramInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentProgramDescription() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentProgramDescription, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentProgramRating() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentProgramRating, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean hasCurrentProgramWithSubtitle() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hasCurrentProgramWithSubtitle, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurAtvSoundSelect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvSoundSelect, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentAudioLang() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentAudioLang, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurInputInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurInputInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentSetting_tv(java.lang.String tvStr) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tvStr);
mRemote.transact(Stub.TRANSACTION_getCurrentSetting_tv, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelFreqCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getChannelFreqCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelFreqByTableIndex(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getChannelFreqByTableIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getChannelchannelNumByTableIndex(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getChannelchannelNumByTableIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelCountByFreq(int freq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(freq);
mRemote.transact(Stub.TRANSACTION_getChannelCountByFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurChannelIndex() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurChannelIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelListChannelCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getChannelListChannelCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getChannelDataList(int iStartIdx, int iContentLen) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iStartIdx);
_data.writeInt(iContentLen);
mRemote.transact(Stub.TRANSACTION_getChannelDataList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean recoverVideoSize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_recoverVideoSize, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getVideoSize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVideoSize, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setVideoSize(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iX);
_data.writeInt(iY);
_data.writeInt(iWidth);
_data.writeInt(iHeight);
mRemote.transact(Stub.TRANSACTION_setVideoSize, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getCurDtvSoundSelectList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurDtvSoundSelectList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurDtvSoundSelectCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurDtvSoundSelectCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurAtvSoundSelectList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvSoundSelectList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurAtvSoundSelectCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurAtvSoundSelectCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setDisplayFreeze(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDisplayFreeze, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setCaptionMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setCaptionMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setAnalogCaption(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_setAnalogCaption, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDigitalCaption(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_setDigitalCaption, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setChannelLockEnable(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setChannelLockEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setChannelFav(int index, boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setChannelFav, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setChannelSkip(int index, boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setChannelSkip, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setChannelBlock(int index, boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setChannelBlock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setChannelDel(int index, boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setChannelDel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getChannelFav(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getChannelFav, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getChannelSkip(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getChannelSkip, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getChannelBlock(int index) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
mRemote.transact(Stub.TRANSACTION_getChannelBlock, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean queryTvStatus(int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_queryTvStatus, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setHdmiAudioSource(int audioSource) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(audioSource);
mRemote.transact(Stub.TRANSACTION_setHdmiAudioSource, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getIsContentLocked() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIsContentLocked, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setSourceLockEnable(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSourceLockEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getSourceLockStatus(int source) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(source);
mRemote.transact(Stub.TRANSACTION_getSourceLockStatus, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setSourceLockStatus(int source, boolean lock) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(source);
_data.writeInt(((lock)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSourceLockStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getSourceLockStatusByIndex(int srcIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcIndex);
mRemote.transact(Stub.TRANSACTION_getSourceLockStatusByIndex, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setSourceLockStatusByIndex(int srcIndex, boolean lock) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(srcIndex);
_data.writeInt(((lock)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSourceLockStatusByIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int startRecordTs(java.lang.String filePath, boolean bWithPreview) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
_data.writeInt(((bWithPreview)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_startRecordTs, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean stopRecordTs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRecordTs, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void getEpgData(int iDayOffset, int iDayCount) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iDayOffset);
_data.writeInt(iDayCount);
mRemote.transact(Stub.TRANSACTION_getEpgData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void getEpgDataByLCN(int u16Lcn, int iDayOffset, int iDayCount) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(u16Lcn);
_data.writeInt(iDayOffset);
_data.writeInt(iDayCount);
mRemote.transact(Stub.TRANSACTION_getEpgDataByLCN, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getEpgListEpgCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEpgListEpgCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setTvTimeZone(float timezone) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(timezone);
mRemote.transact(Stub.TRANSACTION_setTvTimeZone, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getEpgDataList(int iStartIdx, int iContentLen) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(iStartIdx);
_data.writeInt(iContentLen);
mRemote.transact(Stub.TRANSACTION_getEpgDataList, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setParentalLockEnable(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setParentalLockEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getParentalLockEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getParentalLockEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setParentalLockPasswd(int passwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(passwd);
mRemote.transact(Stub.TRANSACTION_setParentalLockPasswd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getParentalLockPasswd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getParentalLockPasswd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setParentalLockRegion(int region) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(region);
mRemote.transact(Stub.TRANSACTION_setParentalLockRegion, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getParentalLockRegion() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getParentalLockRegion, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setParentalRatingDvb(int rating) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(rating);
mRemote.transact(Stub.TRANSACTION_setParentalRatingDvb, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getParentalRatingDvb() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getParentalRatingDvb, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPasswordReverify(boolean isVerified) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isVerified)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setPasswordReverify, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getPasswordReverify() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPasswordReverify, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPasswordVerified(int lockType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(lockType);
mRemote.transact(Stub.TRANSACTION_setPasswordVerified, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getPasswordVerified(int lockType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(lockType);
mRemote.transact(Stub.TRANSACTION_getPasswordVerified, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPipEnable(boolean enable, boolean bKeepSubSourceAlive) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
_data.writeInt(((bKeepSubSourceAlive)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setPipEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getPipEnable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPipEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPipSubSource(int subSource, boolean apply) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subSource);
_data.writeInt(((apply)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setPipSubSource, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getPipSource(int vout) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(vout);
mRemote.transact(Stub.TRANSACTION_getPipSource, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void clearPipSourceIndicator(int source) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(source);
mRemote.transact(Stub.TRANSACTION_clearPipSourceIndicator, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setPipSubMode(int subMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subMode);
mRemote.transact(Stub.TRANSACTION_setPipSubMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getPipSubMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPipSubMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPipSubPosition(int subPos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subPos);
mRemote.transact(Stub.TRANSACTION_setPipSubPosition, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getPipSubPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPipSubPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setPipSubDisplayWindow(int x, int y, int width, int height) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(x);
_data.writeInt(y);
_data.writeInt(width);
_data.writeInt(height);
mRemote.transact(Stub.TRANSACTION_setPipSubDisplayWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlStop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlPause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlResume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlResume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlStartHttp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlStartHttp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void transcodeControlStopHttp() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_transcodeControlStopHttp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getRecoveryCmd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRecoveryCmd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setRecoveryCmd(java.lang.String strCmd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strCmd);
mRemote.transact(Stub.TRANSACTION_setRecoveryCmd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String registerDivX() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_registerDivX, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isDeviceActivated() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDeviceActivated, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String deregisterDivX() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_deregisterDivX, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void enableSpectrumData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableSpectrumData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void disableSpectrumData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disableSpectrumData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public android.app.tv.SpectrumDataInfo getSpectrumInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.app.tv.SpectrumDataInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSpectrumInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.app.tv.SpectrumDataInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void enableGetMediaTotalTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableGetMediaTotalTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void disableGetMediaTotalTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disableGetMediaTotalTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public long getMediaTotalTime(java.lang.String url) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(url);
mRemote.transact(Stub.TRANSACTION_getMediaTotalTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_stopRpcServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_dumpPliMemoryUsage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_suspendRpcServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_resumeRpcServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_restoreSysDefSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setScreenSaverTiming = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_setMenuLanguage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setSerialCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getScreenSaverTiming = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getMenuLanguage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getSerialCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_startPerformanceLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_stopPerformanceLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_clearEeprom = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_readEepToFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_writeFileToEep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_upgradeSystem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getCurAPK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_tvAutoScanStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_tvAutoScanStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_tvAutoScanComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_tvSeekScanStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_tvSeekScanStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_tvScanManualStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_tvScanManualStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_tvScanManualComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_tvScanInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_isTvScanning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_getAtvSeqScanStartFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_getAtvSeqScanEndFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_saveChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_playNextChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_playPrevChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_playFirstChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_playHistoryChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_updateTvChannelList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_getCurChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_getChannelInfoByIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_getChannelCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_sortChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_playChannelByIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_playChannelByNum = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_playChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_playChannelByLCN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_playFirstChannelInFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_playChannelByChnumFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
static final int TRANSACTION_playNumberChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
static final int TRANSACTION_swapChannelByIdxEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
static final int TRANSACTION_swapChannelByNumEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
static final int TRANSACTION_reloadLastPlayedSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
static final int TRANSACTION_setCurChannelSkipped = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
static final int TRANSACTION_setCurAtvSoundStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
static final int TRANSACTION_fineTuneCurFrequency = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
static final int TRANSACTION_setCurChAudioCompensation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
static final int TRANSACTION_setSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
static final int TRANSACTION_setBootSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
static final int TRANSACTION_setSourceAndDisplayWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
static final int TRANSACTION_getCurChannelSkipped = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
static final int TRANSACTION_getCurAtvSoundStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
static final int TRANSACTION_getCurChAudioCompensation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
static final int TRANSACTION_getSourceList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 60);
static final int TRANSACTION_getSourceListCnt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 61);
static final int TRANSACTION_getCurSourceType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 62);
static final int TRANSACTION_getBootSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 63);
static final int TRANSACTION_getCurTvType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 64);
static final int TRANSACTION_getCurLiveSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
static final int TRANSACTION_setBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 66);
static final int TRANSACTION_setContrast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 67);
static final int TRANSACTION_setSaturation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 68);
static final int TRANSACTION_setHue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 69);
static final int TRANSACTION_setSharpness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 70);
static final int TRANSACTION_setColorTempMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 71);
static final int TRANSACTION_setDNR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 72);
static final int TRANSACTION_setAspectRatio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 73);
static final int TRANSACTION_saveAspectRatio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 74);
static final int TRANSACTION_setPictureMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 75);
static final int TRANSACTION_setCurAtvColorStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 76);
static final int TRANSACTION_setAvColorStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 77);
static final int TRANSACTION_setVgaAutoAdjust = (android.os.IBinder.FIRST_CALL_TRANSACTION + 78);
static final int TRANSACTION_setVgaHPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 79);
static final int TRANSACTION_setVgaVPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 80);
static final int TRANSACTION_setVgaPhase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 81);
static final int TRANSACTION_setVgaClock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 82);
static final int TRANSACTION_setMagicPicture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 83);
static final int TRANSACTION_setDCR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 84);
static final int TRANSACTION_setDCC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 85);
static final int TRANSACTION_setBacklight = (android.os.IBinder.FIRST_CALL_TRANSACTION + 86);
static final int TRANSACTION_set3dMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 87);
static final int TRANSACTION_set3dDeep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 88);
static final int TRANSACTION_set3dLRSwap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 89);
static final int TRANSACTION_set3dCvrt2D = (android.os.IBinder.FIRST_CALL_TRANSACTION + 90);
static final int TRANSACTION_set3dStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 91);
static final int TRANSACTION_set3dModeAndChangeRatio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 92);
static final int TRANSACTION_getIs3d = (android.os.IBinder.FIRST_CALL_TRANSACTION + 93);
static final int TRANSACTION_setColorTempRGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 94);
static final int TRANSACTION_setColorTempGGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 95);
static final int TRANSACTION_setColorTempBGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 96);
static final int TRANSACTION_setColorTempROffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 97);
static final int TRANSACTION_setColorTempGOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 98);
static final int TRANSACTION_setColorTempBOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 99);
static final int TRANSACTION_setDisplayWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 100);
static final int TRANSACTION_setDisplayWindowPositionChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 101);
static final int TRANSACTION_setPanelOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 102);
static final int TRANSACTION_scaler_ForceBg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 103);
static final int TRANSACTION_scaler_4k2kOSD_ForceBg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 104);
static final int TRANSACTION_setOverScan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 105);
static final int TRANSACTION_getBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 106);
static final int TRANSACTION_getContrast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 107);
static final int TRANSACTION_getSaturation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 108);
static final int TRANSACTION_getHue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 109);
static final int TRANSACTION_getSharpness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 110);
static final int TRANSACTION_getColorTempLevel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 111);
static final int TRANSACTION_getDNR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 112);
static final int TRANSACTION_getDCC = (android.os.IBinder.FIRST_CALL_TRANSACTION + 113);
static final int TRANSACTION_getPictureMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 114);
static final int TRANSACTION_getCurAtvColorStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 115);
static final int TRANSACTION_getCurAtvColorStdNoAuto = (android.os.IBinder.FIRST_CALL_TRANSACTION + 116);
static final int TRANSACTION_getAvColorStd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 117);
static final int TRANSACTION_getAvColorStdNoAuto = (android.os.IBinder.FIRST_CALL_TRANSACTION + 118);
static final int TRANSACTION_getVgaHPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 119);
static final int TRANSACTION_getVgaVPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 120);
static final int TRANSACTION_getVgaPhase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 121);
static final int TRANSACTION_getVgaClock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 122);
static final int TRANSACTION_getAspectRatio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 123);
static final int TRANSACTION_getMagicPicture = (android.os.IBinder.FIRST_CALL_TRANSACTION + 124);
static final int TRANSACTION_getDCR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 125);
static final int TRANSACTION_getDCRDemoDate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 126);
static final int TRANSACTION_getBacklight = (android.os.IBinder.FIRST_CALL_TRANSACTION + 127);
static final int TRANSACTION_get3dMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 128);
static final int TRANSACTION_get3dDeep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 129);
static final int TRANSACTION_get3dLRSwap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 130);
static final int TRANSACTION_get3dCvrt2D = (android.os.IBinder.FIRST_CALL_TRANSACTION + 131);
static final int TRANSACTION_getPanelOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 132);
static final int TRANSACTION_getResolutionInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 133);
static final int TRANSACTION_checkDviMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 134);
static final int TRANSACTION_getOverScan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 135);
static final int TRANSACTION_setScalerAutoColorRGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 136);
static final int TRANSACTION_setScalerAutoColorGGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 137);
static final int TRANSACTION_setScalerAutoColorBGain = (android.os.IBinder.FIRST_CALL_TRANSACTION + 138);
static final int TRANSACTION_setScalerAutoColorROffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 139);
static final int TRANSACTION_setScalerAutoColorGOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 140);
static final int TRANSACTION_setScalerAutoColorBOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 141);
static final int TRANSACTION_setVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 142);
static final int TRANSACTION_setMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 143);
static final int TRANSACTION_setBalance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 144);
static final int TRANSACTION_setBass = (android.os.IBinder.FIRST_CALL_TRANSACTION + 145);
static final int TRANSACTION_setTreble = (android.os.IBinder.FIRST_CALL_TRANSACTION + 146);
static final int TRANSACTION_setTrueSurround = (android.os.IBinder.FIRST_CALL_TRANSACTION + 147);
static final int TRANSACTION_setClarity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 148);
static final int TRANSACTION_setTrueBass = (android.os.IBinder.FIRST_CALL_TRANSACTION + 149);
static final int TRANSACTION_setSubWoof = (android.os.IBinder.FIRST_CALL_TRANSACTION + 150);
static final int TRANSACTION_setSubWoofVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 151);
static final int TRANSACTION_setEqualizerMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 152);
static final int TRANSACTION_setEqualizer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 153);
static final int TRANSACTION_setOnOffMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 154);
static final int TRANSACTION_setAudioHdmiOutput = (android.os.IBinder.FIRST_CALL_TRANSACTION + 155);
static final int TRANSACTION_setSurroundSound = (android.os.IBinder.FIRST_CALL_TRANSACTION + 156);
static final int TRANSACTION_setAudioSpdifOutput = (android.os.IBinder.FIRST_CALL_TRANSACTION + 157);
static final int TRANSACTION_setWallEffect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 158);
static final int TRANSACTION_setAudioMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 159);
static final int TRANSACTION_setAudioEffect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 160);
static final int TRANSACTION_setKeyToneVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 161);
static final int TRANSACTION_setAudioChannelSwap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 162);
static final int TRANSACTION_setAutoVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 163);
static final int TRANSACTION_getAutoVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 164);
static final int TRANSACTION_getVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 165);
static final int TRANSACTION_getMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 166);
static final int TRANSACTION_getBalance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 167);
static final int TRANSACTION_getBass = (android.os.IBinder.FIRST_CALL_TRANSACTION + 168);
static final int TRANSACTION_getTreble = (android.os.IBinder.FIRST_CALL_TRANSACTION + 169);
static final int TRANSACTION_getEqualizerMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 170);
static final int TRANSACTION_getEqualizer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 171);
static final int TRANSACTION_getTrueSurround = (android.os.IBinder.FIRST_CALL_TRANSACTION + 172);
static final int TRANSACTION_getClarity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 173);
static final int TRANSACTION_getTrueBass = (android.os.IBinder.FIRST_CALL_TRANSACTION + 174);
static final int TRANSACTION_getSubWoof = (android.os.IBinder.FIRST_CALL_TRANSACTION + 175);
static final int TRANSACTION_getSubWoofVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 176);
static final int TRANSACTION_getOnOffMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 177);
static final int TRANSACTION_getWallEffect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 178);
static final int TRANSACTION_getAudioMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 179);
static final int TRANSACTION_getAudioVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 180);
static final int TRANSACTION_getKeyToneVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 181);
static final int TRANSACTION_getAudioChannelSwap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 182);
static final int TRANSACTION_setWLanTmpProfileName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 183);
static final int TRANSACTION_setWLanTmpProfileSSID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 184);
static final int TRANSACTION_setWLanTmpProfileWifiMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 185);
static final int TRANSACTION_setWLanTmpProfileWifiSecurity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 186);
static final int TRANSACTION_setWLanTmpProfileDhcpHostIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 187);
static final int TRANSACTION_setWLanTmpProfileDhcpStartIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 188);
static final int TRANSACTION_setWLanTmpProfileDhcpEndIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 189);
static final int TRANSACTION_setWLanTmpProfileWepIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 190);
static final int TRANSACTION_setWLanProfileCopyToTmp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 191);
static final int TRANSACTION_setWLanProfileCopyFromTmp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 192);
static final int TRANSACTION_setWLanIpAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 193);
static final int TRANSACTION_setWLanProfileActiveIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 194);
static final int TRANSACTION_getWLanProfileName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 195);
static final int TRANSACTION_getWLanProfileSSID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 196);
static final int TRANSACTION_getWLanProfileWifiMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 197);
static final int TRANSACTION_getWLanProfileWifiSecurity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 198);
static final int TRANSACTION_getWLanProfileDhcpHostIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 199);
static final int TRANSACTION_getWLanProfileDhcpStartIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 200);
static final int TRANSACTION_getWLanProfileDhcpEndIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 201);
static final int TRANSACTION_getWLanProfileWepIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 202);
static final int TRANSACTION_getWLanProfileActiveIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 203);
static final int TRANSACTION_getWLanProfileTotalNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 204);
static final int TRANSACTION_getWLanIpAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 205);
static final int TRANSACTION_getWLanDHCPState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 206);
static final int TRANSACTION_getWLanApListSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 207);
static final int TRANSACTION_getWLanApName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 208);
static final int TRANSACTION_getWLanApSecurity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 209);
static final int TRANSACTION_getWLanApStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 210);
static final int TRANSACTION_setWLanWpsMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 211);
static final int TRANSACTION_getWLanWpsMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 212);
static final int TRANSACTION_getWLanPinCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 213);
static final int TRANSACTION_wLanConnectStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 214);
static final int TRANSACTION_wLanConnectStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 215);
static final int TRANSACTION_wLanConnectQueryState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 216);
static final int TRANSACTION_wLan0ActivateState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 217);
static final int TRANSACTION_wiredLanDHCPStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 218);
static final int TRANSACTION_wiredLanDhcpStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 219);
static final int TRANSACTION_wiredLanDhcpQueryState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 220);
static final int TRANSACTION_getWiredLanDhcpEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 221);
static final int TRANSACTION_getWiredLanIpAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 222);
static final int TRANSACTION_getMacAddressInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 223);
static final int TRANSACTION_getMacAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 224);
static final int TRANSACTION_getColorTempData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 225);
static final int TRANSACTION_setColorTempData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 226);
static final int TRANSACTION_setScalerAutoColor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 227);
static final int TRANSACTION_resetColorTemp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 228);
static final int TRANSACTION_setWiredLanManualInit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 229);
static final int TRANSACTION_setWiredLanManualIp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 230);
static final int TRANSACTION_setWiredLanIpAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 231);
static final int TRANSACTION_setMacAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 232);
static final int TRANSACTION_startDecodeImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 233);
static final int TRANSACTION_decodeImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 234);
static final int TRANSACTION_decodeImageEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 235);
static final int TRANSACTION_getDecodeImageResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 236);
static final int TRANSACTION_stopDecodeImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 237);
static final int TRANSACTION_zoomIn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 238);
static final int TRANSACTION_zoomOut = (android.os.IBinder.FIRST_CALL_TRANSACTION + 239);
static final int TRANSACTION_leftRotate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 240);
static final int TRANSACTION_rightRotate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 241);
static final int TRANSACTION_upRotate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 242);
static final int TRANSACTION_downRotate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 243);
static final int TRANSACTION_onZoomMoveUp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 244);
static final int TRANSACTION_onZoomMoveDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 245);
static final int TRANSACTION_onZoomMoveLeft = (android.os.IBinder.FIRST_CALL_TRANSACTION + 246);
static final int TRANSACTION_onZoomMoveRight = (android.os.IBinder.FIRST_CALL_TRANSACTION + 247);
static final int TRANSACTION_enableQFHD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 248);
static final int TRANSACTION_disableQFHD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 249);
static final int TRANSACTION_setSuperResolutionMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 250);
static final int TRANSACTION_getCurQuadFHDMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 251);
static final int TRANSACTION_setCurQuadFHDMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 252);
static final int TRANSACTION_setFunctionParser = (android.os.IBinder.FIRST_CALL_TRANSACTION + 253);
static final int TRANSACTION_getFunctionParser = (android.os.IBinder.FIRST_CALL_TRANSACTION + 254);
static final int TRANSACTION_setOverScanAndAdjustment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 255);
static final int TRANSACTION_getOverScanAndAdjustment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 256);
static final int TRANSACTION_getNoSignalDisplayReady = (android.os.IBinder.FIRST_CALL_TRANSACTION + 257);
static final int TRANSACTION_setEnableBroadcast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 258);
static final int TRANSACTION_setVideoAreaOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 259);
static final int TRANSACTION_setVideoAreaOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 260);
static final int TRANSACTION_getSystemVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 261);
static final int TRANSACTION_getBootcodeVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 262);
static final int TRANSACTION_getEepVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 263);
static final int TRANSACTION_getCpuVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 264);
static final int TRANSACTION_getReleaseDate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 265);
static final int TRANSACTION_getPanelName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 266);
static final int TRANSACTION_getUartMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 267);
static final int TRANSACTION_setUartMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 268);
static final int TRANSACTION_getEepPage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 269);
static final int TRANSACTION_getEepOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 270);
static final int TRANSACTION_getEepData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 271);
static final int TRANSACTION_setEepPage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 272);
static final int TRANSACTION_setEepOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 273);
static final int TRANSACTION_setEepData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 274);
static final int TRANSACTION_startWifi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 275);
static final int TRANSACTION_stopWifi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 276);
static final int TRANSACTION_getWifiState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 277);
static final int TRANSACTION_getNsta = (android.os.IBinder.FIRST_CALL_TRANSACTION + 278);
static final int TRANSACTION_setNsta = (android.os.IBinder.FIRST_CALL_TRANSACTION + 279);
static final int TRANSACTION_getPattern = (android.os.IBinder.FIRST_CALL_TRANSACTION + 280);
static final int TRANSACTION_setPattern = (android.os.IBinder.FIRST_CALL_TRANSACTION + 281);
static final int TRANSACTION_rebootSystem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 282);
static final int TRANSACTION_setFacSingleKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 283);
static final int TRANSACTION_getFacSingleKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 284);
static final int TRANSACTION_setWarmMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 285);
static final int TRANSACTION_exitSkyworthFactorySet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 286);
static final int TRANSACTION_setBusoffMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 287);
static final int TRANSACTION_getFacAutoScanGuide = (android.os.IBinder.FIRST_CALL_TRANSACTION + 288);
static final int TRANSACTION_setFacAutoScanGuide = (android.os.IBinder.FIRST_CALL_TRANSACTION + 289);
static final int TRANSACTION_getFacWarmMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 290);
static final int TRANSACTION_setFacWarmMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 291);
static final int TRANSACTION_getDDREnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 292);
static final int TRANSACTION_setDDREnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 293);
static final int TRANSACTION_getDDRPhaseShift = (android.os.IBinder.FIRST_CALL_TRANSACTION + 294);
static final int TRANSACTION_setDDRPhaseShift = (android.os.IBinder.FIRST_CALL_TRANSACTION + 295);
static final int TRANSACTION_getDDRStep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 296);
static final int TRANSACTION_setDDRStep = (android.os.IBinder.FIRST_CALL_TRANSACTION + 297);
static final int TRANSACTION_getDDRPeriod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 298);
static final int TRANSACTION_setDDRPeriod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 299);
static final int TRANSACTION_getDDROffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 300);
static final int TRANSACTION_setDDROffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 301);
static final int TRANSACTION_getLVDSEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 302);
static final int TRANSACTION_setLVDSEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 303);
static final int TRANSACTION_getLVDSDclkRange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 304);
static final int TRANSACTION_setLVDSDclkRange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 305);
static final int TRANSACTION_getLVDSDclkFMDIV = (android.os.IBinder.FIRST_CALL_TRANSACTION + 306);
static final int TRANSACTION_setLVDSDclkFMDIV = (android.os.IBinder.FIRST_CALL_TRANSACTION + 307);
static final int TRANSACTION_getLVDSNewMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 308);
static final int TRANSACTION_setLVDSNewMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 309);
static final int TRANSACTION_getLVDSPLLOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 310);
static final int TRANSACTION_setLVDSPLLOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 311);
static final int TRANSACTION_getLVDSOnlyEvenOdd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 312);
static final int TRANSACTION_setLVDSOnlyEvenOdd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 313);
static final int TRANSACTION_getLVDSEvenOrOdd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 314);
static final int TRANSACTION_setLVDSEvenOrOdd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 315);
static final int TRANSACTION_getLVDSDrivingCurrent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 316);
static final int TRANSACTION_setLVDSDrivingCurrent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 317);
static final int TRANSACTION_getBARCODE1 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 318);
static final int TRANSACTION_getBARCODE2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 319);
static final int TRANSACTION_getBARCODE3 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 320);
static final int TRANSACTION_getBARCODE4 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 321);
static final int TRANSACTION_setBARCODE1 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 322);
static final int TRANSACTION_setBARCODE2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 323);
static final int TRANSACTION_setBARCODE3 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 324);
static final int TRANSACTION_setBARCODE4 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 325);
static final int TRANSACTION_gldcOsdShow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 326);
static final int TRANSACTION_isKeyDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 327);
static final int TRANSACTION_setInitialFlag = (android.os.IBinder.FIRST_CALL_TRANSACTION + 328);
static final int TRANSACTION_getInitialFlag = (android.os.IBinder.FIRST_CALL_TRANSACTION + 329);
static final int TRANSACTION_getDtvTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 330);
static final int TRANSACTION_setRTKIRMouse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 331);
static final int TRANSACTION_getChannelNameList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 332);
static final int TRANSACTION_getCurrentProgramInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 333);
static final int TRANSACTION_getCurrentProgramDescription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 334);
static final int TRANSACTION_getCurrentProgramRating = (android.os.IBinder.FIRST_CALL_TRANSACTION + 335);
static final int TRANSACTION_hasCurrentProgramWithSubtitle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 336);
static final int TRANSACTION_getCurAtvSoundSelect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 337);
static final int TRANSACTION_getCurrentAudioLang = (android.os.IBinder.FIRST_CALL_TRANSACTION + 338);
static final int TRANSACTION_getCurInputInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 339);
static final int TRANSACTION_getCurrentSetting_tv = (android.os.IBinder.FIRST_CALL_TRANSACTION + 340);
static final int TRANSACTION_getChannelFreqCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 341);
static final int TRANSACTION_getChannelFreqByTableIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 342);
static final int TRANSACTION_getChannelchannelNumByTableIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 343);
static final int TRANSACTION_getChannelCountByFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 344);
static final int TRANSACTION_getCurChannelIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 345);
static final int TRANSACTION_getChannelListChannelCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 346);
static final int TRANSACTION_getChannelDataList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 347);
static final int TRANSACTION_recoverVideoSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 348);
static final int TRANSACTION_getVideoSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 349);
static final int TRANSACTION_setVideoSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 350);
static final int TRANSACTION_getCurDtvSoundSelectList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 351);
static final int TRANSACTION_getCurDtvSoundSelectCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 352);
static final int TRANSACTION_getCurAtvSoundSelectList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 353);
static final int TRANSACTION_getCurAtvSoundSelectCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 354);
static final int TRANSACTION_setDisplayFreeze = (android.os.IBinder.FIRST_CALL_TRANSACTION + 355);
static final int TRANSACTION_setCaptionMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 356);
static final int TRANSACTION_setAnalogCaption = (android.os.IBinder.FIRST_CALL_TRANSACTION + 357);
static final int TRANSACTION_setDigitalCaption = (android.os.IBinder.FIRST_CALL_TRANSACTION + 358);
static final int TRANSACTION_setChannelLockEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 359);
static final int TRANSACTION_setChannelFav = (android.os.IBinder.FIRST_CALL_TRANSACTION + 360);
static final int TRANSACTION_setChannelSkip = (android.os.IBinder.FIRST_CALL_TRANSACTION + 361);
static final int TRANSACTION_setChannelBlock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 362);
static final int TRANSACTION_setChannelDel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 363);
static final int TRANSACTION_getChannelFav = (android.os.IBinder.FIRST_CALL_TRANSACTION + 364);
static final int TRANSACTION_getChannelSkip = (android.os.IBinder.FIRST_CALL_TRANSACTION + 365);
static final int TRANSACTION_getChannelBlock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 366);
static final int TRANSACTION_queryTvStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 367);
static final int TRANSACTION_setHdmiAudioSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 368);
static final int TRANSACTION_getIsContentLocked = (android.os.IBinder.FIRST_CALL_TRANSACTION + 369);
static final int TRANSACTION_setSourceLockEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 370);
static final int TRANSACTION_getSourceLockStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 371);
static final int TRANSACTION_setSourceLockStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 372);
static final int TRANSACTION_getSourceLockStatusByIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 373);
static final int TRANSACTION_setSourceLockStatusByIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 374);
static final int TRANSACTION_startRecordTs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 375);
static final int TRANSACTION_stopRecordTs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 376);
static final int TRANSACTION_getEpgData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 377);
static final int TRANSACTION_getEpgDataByLCN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 378);
static final int TRANSACTION_getEpgListEpgCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 379);
static final int TRANSACTION_setTvTimeZone = (android.os.IBinder.FIRST_CALL_TRANSACTION + 380);
static final int TRANSACTION_getEpgDataList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 381);
static final int TRANSACTION_setParentalLockEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 382);
static final int TRANSACTION_getParentalLockEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 383);
static final int TRANSACTION_setParentalLockPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 384);
static final int TRANSACTION_getParentalLockPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 385);
static final int TRANSACTION_setParentalLockRegion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 386);
static final int TRANSACTION_getParentalLockRegion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 387);
static final int TRANSACTION_setParentalRatingDvb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 388);
static final int TRANSACTION_getParentalRatingDvb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 389);
static final int TRANSACTION_setPasswordReverify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 390);
static final int TRANSACTION_getPasswordReverify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 391);
static final int TRANSACTION_setPasswordVerified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 392);
static final int TRANSACTION_getPasswordVerified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 393);
static final int TRANSACTION_setPipEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 394);
static final int TRANSACTION_getPipEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 395);
static final int TRANSACTION_setPipSubSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 396);
static final int TRANSACTION_getPipSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 397);
static final int TRANSACTION_clearPipSourceIndicator = (android.os.IBinder.FIRST_CALL_TRANSACTION + 398);
static final int TRANSACTION_setPipSubMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 399);
static final int TRANSACTION_getPipSubMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 400);
static final int TRANSACTION_setPipSubPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 401);
static final int TRANSACTION_getPipSubPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 402);
static final int TRANSACTION_setPipSubDisplayWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 403);
static final int TRANSACTION_transcodeControlStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 404);
static final int TRANSACTION_transcodeControlStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 405);
static final int TRANSACTION_transcodeControlPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 406);
static final int TRANSACTION_transcodeControlResume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 407);
static final int TRANSACTION_transcodeControlStartHttp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 408);
static final int TRANSACTION_transcodeControlStopHttp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 409);
static final int TRANSACTION_getRecoveryCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 410);
static final int TRANSACTION_setRecoveryCmd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 411);
static final int TRANSACTION_registerDivX = (android.os.IBinder.FIRST_CALL_TRANSACTION + 412);
static final int TRANSACTION_isDeviceActivated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 413);
static final int TRANSACTION_deregisterDivX = (android.os.IBinder.FIRST_CALL_TRANSACTION + 414);
static final int TRANSACTION_enableSpectrumData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 415);
static final int TRANSACTION_disableSpectrumData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 416);
static final int TRANSACTION_getSpectrumInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 417);
static final int TRANSACTION_enableGetMediaTotalTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 418);
static final int TRANSACTION_disableGetMediaTotalTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 419);
static final int TRANSACTION_getMediaTotalTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 420);
}
//SystemCommandExecutor

public void stopRpcServer() throws android.os.RemoteException;
public void dumpPliMemoryUsage() throws android.os.RemoteException;
public void suspendRpcServer() throws android.os.RemoteException;
public void resumeRpcServer() throws android.os.RemoteException;
public void restoreSysDefSetting() throws android.os.RemoteException;
public void setScreenSaverTiming(int itiming) throws android.os.RemoteException;
public void setMenuLanguage(int iLanguage) throws android.os.RemoteException;
public void setSerialCode(java.lang.String strSerCode) throws android.os.RemoteException;
public int getScreenSaverTiming() throws android.os.RemoteException;
public int getMenuLanguage() throws android.os.RemoteException;
public java.lang.String getSerialCode() throws android.os.RemoteException;
public void startPerformanceLog() throws android.os.RemoteException;
public void stopPerformanceLog(java.lang.String strFilePath) throws android.os.RemoteException;
public void clearEeprom() throws android.os.RemoteException;
public boolean readEepToFile() throws android.os.RemoteException;
public boolean writeFileToEep() throws android.os.RemoteException;
public void upgradeSystem() throws android.os.RemoteException;
public java.lang.String getCurAPK() throws android.os.RemoteException;
//TvChannelApiExecutor

public boolean tvAutoScanStart(int tvType, boolean bUpdateScan) throws android.os.RemoteException;
public void tvAutoScanStop() throws android.os.RemoteException;
public void tvAutoScanComplete() throws android.os.RemoteException;
public boolean tvSeekScanStart(boolean bSeekForward) throws android.os.RemoteException;
public void tvSeekScanStop() throws android.os.RemoteException;
public boolean tvScanManualStart(int iFreq, int iBandWidth, int iPhyChNum) throws android.os.RemoteException;
public void tvScanManualStop() throws android.os.RemoteException;
public void tvScanManualComplete() throws android.os.RemoteException;
public int tvScanInfo(int infoId) throws android.os.RemoteException;
public boolean isTvScanning() throws android.os.RemoteException;
public int getAtvSeqScanStartFreq() throws android.os.RemoteException;
public int getAtvSeqScanEndFreq() throws android.os.RemoteException;
public void saveChannel() throws android.os.RemoteException;
public void playNextChannel() throws android.os.RemoteException;
public void playPrevChannel() throws android.os.RemoteException;
public void playFirstChannel() throws android.os.RemoteException;
public void playHistoryChannel() throws android.os.RemoteException;
public void updateTvChannelList(android.app.tv.ChannelFilter filter) throws android.os.RemoteException;
public android.app.tv.ChannelInfo getCurChannel() throws android.os.RemoteException;
public android.app.tv.ChannelInfo getChannelInfoByIndex(int iIndex) throws android.os.RemoteException;
public int getChannelCount() throws android.os.RemoteException;
public boolean sortChannel(int policy) throws android.os.RemoteException;
public void playChannelByIndex(int iIndex) throws android.os.RemoteException;
public void playChannelByNum(int iNum) throws android.os.RemoteException;
public boolean playChannel(int iIndex) throws android.os.RemoteException;
public boolean playChannelByLCN(int iLcnNum) throws android.os.RemoteException;
public boolean playFirstChannelInFreq(int iFreq) throws android.os.RemoteException;
public boolean playChannelByChnumFreq(int iSysChNum, int iFreq, java.lang.String tvSystem) throws android.os.RemoteException;
public boolean playNumberChannel(int majorNum, int minorNum, boolean isAudioFocus) throws android.os.RemoteException;
public void swapChannelByIdxEx(int iChIdx, boolean bSwapChNum, boolean bPlayAfterSwap) throws android.os.RemoteException;
public void swapChannelByNumEx(int iChNum, boolean bSwapChNum, boolean bPlayAfterSwap) throws android.os.RemoteException;
public void reloadLastPlayedSource() throws android.os.RemoteException;
public void setCurChannelSkipped(boolean bSkip) throws android.os.RemoteException;
public void setCurAtvSoundStd(int soundSystemId) throws android.os.RemoteException;
public void fineTuneCurFrequency(int iOffset, boolean bPerminant) throws android.os.RemoteException;
public void setCurChAudioCompensation(int iValue, boolean bApply) throws android.os.RemoteException;
public boolean setSource(int iIndex) throws android.os.RemoteException;
public void setBootSource(int sourceOpt) throws android.os.RemoteException;
public boolean setSourceAndDisplayWindow(int src, int x, int y, int width, int height) throws android.os.RemoteException;
public boolean getCurChannelSkipped() throws android.os.RemoteException;
public int getCurAtvSoundStd() throws android.os.RemoteException;
public int getCurChAudioCompensation() throws android.os.RemoteException;
public java.lang.String getSourceList() throws android.os.RemoteException;
public int getSourceListCnt(boolean bWithoutPlayback) throws android.os.RemoteException;
public long getCurSourceType() throws android.os.RemoteException;
public int getBootSource() throws android.os.RemoteException;
public int getCurTvType() throws android.os.RemoteException;
public int getCurLiveSource() throws android.os.RemoteException;
//TvDisplaySetupApiExecutor

public void setBrightness(int iValue) throws android.os.RemoteException;
public void setContrast(int iValue) throws android.os.RemoteException;
public void setSaturation(int iValue) throws android.os.RemoteException;
public void setHue(int iValue) throws android.os.RemoteException;
public void setSharpness(boolean iApply, int iValue) throws android.os.RemoteException;
public void setColorTempMode(int level) throws android.os.RemoteException;
public void setDNR(int mode) throws android.os.RemoteException;
public void setAspectRatio(int ratio) throws android.os.RemoteException;
public void saveAspectRatio(int ratio) throws android.os.RemoteException;
public void setPictureMode(int mode) throws android.os.RemoteException;
public void setCurAtvColorStd(int colorStd) throws android.os.RemoteException;
public void setAvColorStd(int colorStd) throws android.os.RemoteException;
public boolean setVgaAutoAdjust() throws android.os.RemoteException;
public boolean setVgaHPosition(char ucPosition) throws android.os.RemoteException;
public boolean setVgaVPosition(char ucPosition) throws android.os.RemoteException;
public boolean setVgaPhase(char ucValue) throws android.os.RemoteException;
public boolean setVgaClock(char ucValue) throws android.os.RemoteException;
public void setMagicPicture(int magicPic) throws android.os.RemoteException;
public void setDCR(int iDCR) throws android.os.RemoteException;
public void setDCC(boolean iDccOn, boolean iIsApply) throws android.os.RemoteException;
public void setBacklight(int iValue) throws android.os.RemoteException;
public void set3dMode(int imode) throws android.os.RemoteException;
public void set3dDeep(int imode) throws android.os.RemoteException;
public void set3dLRSwap(boolean bOn) throws android.os.RemoteException;
public boolean set3dCvrt2D(boolean bOn, int iFrameFlag) throws android.os.RemoteException;
public void set3dStrength(int iStrength) throws android.os.RemoteException;
public boolean set3dModeAndChangeRatio(int iMode, boolean bMute, int iType) throws android.os.RemoteException;
public boolean getIs3d() throws android.os.RemoteException;
public void setColorTempRGain(int iValue) throws android.os.RemoteException;
public void setColorTempGGain(int iValue) throws android.os.RemoteException;
public void setColorTempBGain(int iValue) throws android.os.RemoteException;
public void setColorTempROffset(int iValue) throws android.os.RemoteException;
public void setColorTempGOffset(int iValue) throws android.os.RemoteException;
public void setColorTempBOffset(int iValue) throws android.os.RemoteException;
public void setDisplayWindow(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException;
public void setDisplayWindowPositionChange(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException;
public void setPanelOn(boolean bOn) throws android.os.RemoteException;
public void scaler_ForceBg(boolean bOn) throws android.os.RemoteException;
public void scaler_4k2kOSD_ForceBg(boolean bOn) throws android.os.RemoteException;
public void setOverScan(int h_start, int h_width, int v_start, int v_length) throws android.os.RemoteException;
public int getBrightness() throws android.os.RemoteException;
public int getContrast() throws android.os.RemoteException;
public int getSaturation() throws android.os.RemoteException;
public int getHue() throws android.os.RemoteException;
public int getSharpness() throws android.os.RemoteException;
public int getColorTempLevel() throws android.os.RemoteException;
public int getDNR() throws android.os.RemoteException;
public boolean getDCC() throws android.os.RemoteException;
public int getPictureMode() throws android.os.RemoteException;
public int getCurAtvColorStd() throws android.os.RemoteException;
public int getCurAtvColorStdNoAuto() throws android.os.RemoteException;
public int getAvColorStd() throws android.os.RemoteException;
public int getAvColorStdNoAuto() throws android.os.RemoteException;
public char getVgaHPosition() throws android.os.RemoteException;
public char getVgaVPosition() throws android.os.RemoteException;
public char getVgaPhase() throws android.os.RemoteException;
public char getVgaClock() throws android.os.RemoteException;
public int getAspectRatio(int iSourceOption) throws android.os.RemoteException;
public int getMagicPicture() throws android.os.RemoteException;
public int getDCR() throws android.os.RemoteException;
public java.lang.String getDCRDemoDate() throws android.os.RemoteException;
public int getBacklight() throws android.os.RemoteException;
public int get3dMode() throws android.os.RemoteException;
public int get3dDeep() throws android.os.RemoteException;
public boolean get3dLRSwap() throws android.os.RemoteException;
public boolean get3dCvrt2D() throws android.os.RemoteException;
public boolean getPanelOn() throws android.os.RemoteException;
public java.lang.String getResolutionInfo() throws android.os.RemoteException;
public boolean checkDviMode() throws android.os.RemoteException;
public java.lang.String getOverScan() throws android.os.RemoteException;
//TvFactoryApiExecutor

public void setScalerAutoColorRGain(int iValue) throws android.os.RemoteException;
public void setScalerAutoColorGGain(int iValue) throws android.os.RemoteException;
public void setScalerAutoColorBGain(int iValue) throws android.os.RemoteException;
public void setScalerAutoColorROffset(int iValue) throws android.os.RemoteException;
public void setScalerAutoColorGOffset(int iValue) throws android.os.RemoteException;
public void setScalerAutoColorBOffset(int iValue) throws android.os.RemoteException;
//TvSoundSetupApiExecutor

public void setVolume(int iValue) throws android.os.RemoteException;
public void setMute(boolean mute) throws android.os.RemoteException;
public void setBalance(int iValue) throws android.os.RemoteException;
public void setBass(int iValue) throws android.os.RemoteException;
public void setTreble(int iValue) throws android.os.RemoteException;
public void setTrueSurround(boolean bEnable) throws android.os.RemoteException;
public void setClarity(boolean bEnable) throws android.os.RemoteException;
public void setTrueBass(boolean bEnable) throws android.os.RemoteException;
public void setSubWoof(boolean bEnable) throws android.os.RemoteException;
public void setSubWoofVolume(int iValue) throws android.os.RemoteException;
public void setEqualizerMode(int iMode) throws android.os.RemoteException;
public void setEqualizer(int iFreq, int iValue) throws android.os.RemoteException;
public void setOnOffMusic(boolean bOn) throws android.os.RemoteException;
public void setAudioHdmiOutput(int mode) throws android.os.RemoteException;
public void setSurroundSound(int mode) throws android.os.RemoteException;
public void setAudioSpdifOutput(int mode) throws android.os.RemoteException;
public void setWallEffect(boolean bEnable) throws android.os.RemoteException;
public void setAudioMode(int mode) throws android.os.RemoteException;
public void setAudioEffect(int audioEffect, int param) throws android.os.RemoteException;
public void setKeyToneVolume(int iVol) throws android.os.RemoteException;
public void setAudioChannelSwap(int sel) throws android.os.RemoteException;
public boolean setAutoVolume(boolean bEnable) throws android.os.RemoteException;
public boolean getAutoVolume() throws android.os.RemoteException;
public int getVolume() throws android.os.RemoteException;
public boolean getMute() throws android.os.RemoteException;
public int getBalance() throws android.os.RemoteException;
public int getBass() throws android.os.RemoteException;
public int getTreble() throws android.os.RemoteException;
public int getEqualizerMode() throws android.os.RemoteException;
public int getEqualizer(int iFreq) throws android.os.RemoteException;
public boolean getTrueSurround() throws android.os.RemoteException;
public boolean getClarity() throws android.os.RemoteException;
public boolean getTrueBass() throws android.os.RemoteException;
public boolean getSubWoof() throws android.os.RemoteException;
public int getSubWoofVolume() throws android.os.RemoteException;
public boolean getOnOffMusic() throws android.os.RemoteException;
public boolean getWallEffect() throws android.os.RemoteException;
public int getAudioMode() throws android.os.RemoteException;
public boolean getAudioVolume() throws android.os.RemoteException;
public int getKeyToneVolume() throws android.os.RemoteException;
public int getAudioChannelSwap() throws android.os.RemoteException;
//Invoke WLanSetupApiExecutor functions

public void setWLanTmpProfileName(java.lang.String pStrName) throws android.os.RemoteException;
public void setWLanTmpProfileSSID(java.lang.String pStrSSID) throws android.os.RemoteException;
public void setWLanTmpProfileWifiMode(int mode) throws android.os.RemoteException;
public void setWLanTmpProfileWifiSecurity(int security, java.lang.String pStrKey) throws android.os.RemoteException;
public void setWLanTmpProfileDhcpHostIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException;
public void setWLanTmpProfileDhcpStartIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException;
public void setWLanTmpProfileDhcpEndIp(char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException;
public void setWLanTmpProfileWepIndex(int iIndex) throws android.os.RemoteException;
public void setWLanProfileCopyToTmp(int iProfileIndex) throws android.os.RemoteException;
public void setWLanProfileCopyFromTmp(int iProfileIndex) throws android.os.RemoteException;
public void setWLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException;
public void setWLanProfileActiveIndex(int iProfileIndex) throws android.os.RemoteException;
public java.lang.String getWLanProfileName(int iProfileIndex) throws android.os.RemoteException;
public java.lang.String getWLanProfileSSID(int iProfileIndex) throws android.os.RemoteException;
public int getWLanProfileWifiMode(int iProfileIndex) throws android.os.RemoteException;
public int getWLanProfileWifiSecurity(int iProfileIndex) throws android.os.RemoteException;
public java.lang.String getWLanProfileDhcpHostIp(int iProfileIndex) throws android.os.RemoteException;
public java.lang.String getWLanProfileDhcpStartIp(int iProfileIndex) throws android.os.RemoteException;
public java.lang.String getWLanProfileDhcpEndIp(int iProfileIndex) throws android.os.RemoteException;
public int getWLanProfileWepIndex(int iProfileIndex) throws android.os.RemoteException;
public int getWLanProfileActiveIndex() throws android.os.RemoteException;
public int getWLanProfileTotalNumber() throws android.os.RemoteException;
public java.lang.String getWLanIpAddr(int netType) throws android.os.RemoteException;
public boolean getWLanDHCPState() throws android.os.RemoteException;
public int getWLanApListSize() throws android.os.RemoteException;
public java.lang.String getWLanApName(int iApIndex) throws android.os.RemoteException;
public int getWLanApSecurity(int iApIndex) throws android.os.RemoteException;
public int getWLanApStrength(int iApIndex) throws android.os.RemoteException;
public void setWLanWpsMode(int mode) throws android.os.RemoteException;
public int getWLanWpsMode() throws android.os.RemoteException;
public java.lang.String getWLanPinCode() throws android.os.RemoteException;
public void wLanConnectStart(int iProfileIndex) throws android.os.RemoteException;
public void wLanConnectStop(boolean bForce) throws android.os.RemoteException;
public int wLanConnectQueryState() throws android.os.RemoteException;
public int wLan0ActivateState() throws android.os.RemoteException;
//LanSetupApiExecutor

public boolean wiredLanDHCPStart() throws android.os.RemoteException;
public void wiredLanDhcpStop(boolean bForceStop) throws android.os.RemoteException;
public int wiredLanDhcpQueryState() throws android.os.RemoteException;
public boolean getWiredLanDhcpEnable() throws android.os.RemoteException;
public java.lang.String getWiredLanIpAddr(int netType, boolean bFromDatabase) throws android.os.RemoteException;
public java.lang.String getMacAddressInfo(int iNetInterface) throws android.os.RemoteException;
public java.lang.String getMacAddress() throws android.os.RemoteException;
//tc_tan

public int getColorTempData(int index) throws android.os.RemoteException;
public void setColorTempData(int index, int iValue) throws android.os.RemoteException;
public boolean setScalerAutoColor() throws android.os.RemoteException;
public void resetColorTemp() throws android.os.RemoteException;
public void setWiredLanManualInit() throws android.os.RemoteException;
public void setWiredLanManualIp() throws android.os.RemoteException;
public void setWiredLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4) throws android.os.RemoteException;
public void setMacAddress(char mac1, char mac2, char mac3, char mac4, char mac5, char mac6) throws android.os.RemoteException;
//ImageDecoderApiExecutor

public long startDecodeImage(boolean bBackupHttpFileSource) throws android.os.RemoteException;
public void decodeImage(java.lang.String pFilePath, int transitionType) throws android.os.RemoteException;
public void decodeImageEx(java.lang.String pFilePath, int transitionType, boolean bUpnpFile) throws android.os.RemoteException;
public long getDecodeImageResult() throws android.os.RemoteException;
public void stopDecodeImage() throws android.os.RemoteException;
public void zoomIn() throws android.os.RemoteException;
public void zoomOut() throws android.os.RemoteException;
public void leftRotate() throws android.os.RemoteException;
public void rightRotate() throws android.os.RemoteException;
public void upRotate() throws android.os.RemoteException;
public void downRotate() throws android.os.RemoteException;
public void onZoomMoveUp() throws android.os.RemoteException;
public void onZoomMoveDown() throws android.os.RemoteException;
public void onZoomMoveLeft() throws android.os.RemoteException;
public void onZoomMoveRight() throws android.os.RemoteException;
public void enableQFHD() throws android.os.RemoteException;
//Request by FengWen that control Qual-FHD enable/disable

public void disableQFHD() throws android.os.RemoteException;
public void setSuperResolutionMode(boolean enable) throws android.os.RemoteException;
public int getCurQuadFHDMode() throws android.os.RemoteException;
public boolean setCurQuadFHDMode(int mode) throws android.os.RemoteException;
public void setFunctionParser(int paramcounter, java.lang.String param) throws android.os.RemoteException;
public java.lang.String getFunctionParser(int paramcounter, java.lang.String param) throws android.os.RemoteException;
public void setOverScanAndAdjustment(int h_ratio, int v_ratio, int h_start, int h_width, int v_start, int v_length, boolean applyalltiming, int customer) throws android.os.RemoteException;
public java.lang.String getOverScanAndAdjustment(int customer) throws android.os.RemoteException;
//NoSignal DisplayReady

public boolean getNoSignalDisplayReady() throws android.os.RemoteException;
public void setEnableBroadcast(boolean enable) throws android.os.RemoteException;
//Set interesting area of video

public void setVideoAreaOn(int x, int y, int w, int h, int plane) throws android.os.RemoteException;
public void setVideoAreaOff(int plane) throws android.os.RemoteException;
//Test class get set
//IcData getSetClass(IcData classData);	
//factory menu api

public java.lang.String getSystemVersion() throws android.os.RemoteException;
public java.lang.String getBootcodeVersion() throws android.os.RemoteException;
public java.lang.String getEepVersion() throws android.os.RemoteException;
public java.lang.String getCpuVersion() throws android.os.RemoteException;
public java.lang.String getReleaseDate() throws android.os.RemoteException;
public java.lang.String getPanelName() throws android.os.RemoteException;
public int getUartMode() throws android.os.RemoteException;
public void setUartMode(int iValue) throws android.os.RemoteException;
public int getEepPage() throws android.os.RemoteException;
public int getEepOffset() throws android.os.RemoteException;
public int getEepData() throws android.os.RemoteException;
public void setEepPage(int iValue) throws android.os.RemoteException;
public void setEepOffset(int iValue) throws android.os.RemoteException;
public void setEepData(int iValue) throws android.os.RemoteException;
public void startWifi() throws android.os.RemoteException;
public void stopWifi() throws android.os.RemoteException;
public boolean getWifiState() throws android.os.RemoteException;
public int getNsta(int type) throws android.os.RemoteException;
public void setNsta(int type, int iValue) throws android.os.RemoteException;
public int getPattern() throws android.os.RemoteException;
public void setPattern(int iValue) throws android.os.RemoteException;
public void rebootSystem() throws android.os.RemoteException;
public void setFacSingleKey(boolean mode) throws android.os.RemoteException;
public boolean getFacSingleKey() throws android.os.RemoteException;
public void setWarmMode(boolean mode) throws android.os.RemoteException;
public void exitSkyworthFactorySet() throws android.os.RemoteException;
public void setBusoffMode(boolean mode) throws android.os.RemoteException;
public boolean getFacAutoScanGuide() throws android.os.RemoteException;
public void setFacAutoScanGuide(boolean mode) throws android.os.RemoteException;
public boolean getFacWarmMode() throws android.os.RemoteException;
public void setFacWarmMode(boolean mode) throws android.os.RemoteException;
public boolean getDDREnable() throws android.os.RemoteException;
public void setDDREnable(boolean mode) throws android.os.RemoteException;
public boolean getDDRPhaseShift() throws android.os.RemoteException;
public void setDDRPhaseShift(boolean mode) throws android.os.RemoteException;
public int getDDRStep() throws android.os.RemoteException;
public void setDDRStep(int iValue) throws android.os.RemoteException;
public int getDDRPeriod() throws android.os.RemoteException;
public void setDDRPeriod(int iValue) throws android.os.RemoteException;
public int getDDROffset() throws android.os.RemoteException;
public void setDDROffset(int iValue) throws android.os.RemoteException;
public boolean getLVDSEnable() throws android.os.RemoteException;
public void setLVDSEnable(boolean mode) throws android.os.RemoteException;
public int getLVDSDclkRange() throws android.os.RemoteException;
public void setLVDSDclkRange(int iValue) throws android.os.RemoteException;
public int getLVDSDclkFMDIV() throws android.os.RemoteException;
public void setLVDSDclkFMDIV(int iValue) throws android.os.RemoteException;
public boolean getLVDSNewMode() throws android.os.RemoteException;
public void setLVDSNewMode(boolean mode) throws android.os.RemoteException;
public int getLVDSPLLOffset() throws android.os.RemoteException;
public void setLVDSPLLOffset(int iValue) throws android.os.RemoteException;
public boolean getLVDSOnlyEvenOdd() throws android.os.RemoteException;
public void setLVDSOnlyEvenOdd(boolean mode) throws android.os.RemoteException;
public boolean getLVDSEvenOrOdd() throws android.os.RemoteException;
public void setLVDSEvenOrOdd(boolean mode) throws android.os.RemoteException;
public int getLVDSDrivingCurrent() throws android.os.RemoteException;
public void setLVDSDrivingCurrent(int iValue) throws android.os.RemoteException;
public java.lang.String getBARCODE1() throws android.os.RemoteException;
public java.lang.String getBARCODE2() throws android.os.RemoteException;
public java.lang.String getBARCODE3() throws android.os.RemoteException;
public java.lang.String getBARCODE4() throws android.os.RemoteException;
public void setBARCODE1(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException;
public void setBARCODE2(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException;
public void setBARCODE3(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException;
public void setBARCODE4(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8) throws android.os.RemoteException;
public boolean gldcOsdShow(int mode) throws android.os.RemoteException;
public boolean isKeyDown(int key) throws android.os.RemoteException;
public void setInitialFlag(boolean bInitial) throws android.os.RemoteException;
public boolean getInitialFlag() throws android.os.RemoteException;
public int getDtvTime() throws android.os.RemoteException;
public boolean setRTKIRMouse(boolean setting) throws android.os.RemoteException;
public java.lang.String getChannelNameList(int iStartIdx, int iContentLen, boolean bFilter) throws android.os.RemoteException;
public java.lang.String getCurrentProgramInfo() throws android.os.RemoteException;
public java.lang.String getCurrentProgramDescription() throws android.os.RemoteException;
public java.lang.String getCurrentProgramRating() throws android.os.RemoteException;
public boolean hasCurrentProgramWithSubtitle() throws android.os.RemoteException;
public java.lang.String getCurAtvSoundSelect() throws android.os.RemoteException;
public java.lang.String getCurrentAudioLang() throws android.os.RemoteException;
public java.lang.String getCurInputInfo() throws android.os.RemoteException;
public java.lang.String getCurrentSetting_tv(java.lang.String tvStr) throws android.os.RemoteException;
public int getChannelFreqCount() throws android.os.RemoteException;
public int getChannelFreqByTableIndex(int index) throws android.os.RemoteException;
public java.lang.String getChannelchannelNumByTableIndex(int index) throws android.os.RemoteException;
public int getChannelCountByFreq(int freq) throws android.os.RemoteException;
public int getCurChannelIndex() throws android.os.RemoteException;
public int getChannelListChannelCount() throws android.os.RemoteException;
public java.lang.String getChannelDataList(int iStartIdx, int iContentLen) throws android.os.RemoteException;
public boolean recoverVideoSize() throws android.os.RemoteException;
public java.lang.String getVideoSize() throws android.os.RemoteException;
public void setVideoSize(int iX, int iY, int iWidth, int iHeight) throws android.os.RemoteException;
public java.lang.String getCurDtvSoundSelectList() throws android.os.RemoteException;
public int getCurDtvSoundSelectCount() throws android.os.RemoteException;
public java.lang.String getCurAtvSoundSelectList() throws android.os.RemoteException;
public int getCurAtvSoundSelectCount() throws android.os.RemoteException;
public boolean setDisplayFreeze(boolean enable) throws android.os.RemoteException;
public void setCaptionMode(int mode) throws android.os.RemoteException;
public void setAnalogCaption(int type) throws android.os.RemoteException;
public void setDigitalCaption(int type) throws android.os.RemoteException;
public void setChannelLockEnable(boolean enable) throws android.os.RemoteException;
public void setChannelFav(int index, boolean enable) throws android.os.RemoteException;
public void setChannelSkip(int index, boolean enable) throws android.os.RemoteException;
public void setChannelBlock(int index, boolean enable) throws android.os.RemoteException;
public void setChannelDel(int index, boolean enable) throws android.os.RemoteException;
public boolean getChannelFav(int index) throws android.os.RemoteException;
public boolean getChannelSkip(int index) throws android.os.RemoteException;
public boolean getChannelBlock(int index) throws android.os.RemoteException;
public boolean queryTvStatus(int type) throws android.os.RemoteException;
public void setHdmiAudioSource(int audioSource) throws android.os.RemoteException;
public boolean getIsContentLocked() throws android.os.RemoteException;
public void setSourceLockEnable(boolean enable) throws android.os.RemoteException;
public boolean getSourceLockStatus(int source) throws android.os.RemoteException;
public void setSourceLockStatus(int source, boolean lock) throws android.os.RemoteException;
public boolean getSourceLockStatusByIndex(int srcIndex) throws android.os.RemoteException;
public void setSourceLockStatusByIndex(int srcIndex, boolean lock) throws android.os.RemoteException;
public int startRecordTs(java.lang.String filePath, boolean bWithPreview) throws android.os.RemoteException;
public boolean stopRecordTs() throws android.os.RemoteException;
public void getEpgData(int iDayOffset, int iDayCount) throws android.os.RemoteException;
public void getEpgDataByLCN(int u16Lcn, int iDayOffset, int iDayCount) throws android.os.RemoteException;
public int getEpgListEpgCount() throws android.os.RemoteException;
public void setTvTimeZone(float timezone) throws android.os.RemoteException;
public java.lang.String getEpgDataList(int iStartIdx, int iContentLen) throws android.os.RemoteException;
public void setParentalLockEnable(boolean enable) throws android.os.RemoteException;
public boolean getParentalLockEnable() throws android.os.RemoteException;
public void setParentalLockPasswd(int passwd) throws android.os.RemoteException;
public int getParentalLockPasswd() throws android.os.RemoteException;
public void setParentalLockRegion(int region) throws android.os.RemoteException;
public int getParentalLockRegion() throws android.os.RemoteException;
public void setParentalRatingDvb(int rating) throws android.os.RemoteException;
public int getParentalRatingDvb() throws android.os.RemoteException;
public void setPasswordReverify(boolean isVerified) throws android.os.RemoteException;
public boolean getPasswordReverify() throws android.os.RemoteException;
public void setPasswordVerified(int lockType) throws android.os.RemoteException;
public boolean getPasswordVerified(int lockType) throws android.os.RemoteException;
public void setPipEnable(boolean enable, boolean bKeepSubSourceAlive) throws android.os.RemoteException;
public boolean getPipEnable() throws android.os.RemoteException;
public void setPipSubSource(int subSource, boolean apply) throws android.os.RemoteException;
public int getPipSource(int vout) throws android.os.RemoteException;
public void clearPipSourceIndicator(int source) throws android.os.RemoteException;
public void setPipSubMode(int subMode) throws android.os.RemoteException;
public int getPipSubMode() throws android.os.RemoteException;
public void setPipSubPosition(int subPos) throws android.os.RemoteException;
public int getPipSubPosition() throws android.os.RemoteException;
public void setPipSubDisplayWindow(int x, int y, int width, int height) throws android.os.RemoteException;
public void transcodeControlStart() throws android.os.RemoteException;
public void transcodeControlStop() throws android.os.RemoteException;
public void transcodeControlPause() throws android.os.RemoteException;
public void transcodeControlResume() throws android.os.RemoteException;
public void transcodeControlStartHttp() throws android.os.RemoteException;
public void transcodeControlStopHttp() throws android.os.RemoteException;
public java.lang.String getRecoveryCmd() throws android.os.RemoteException;
public void setRecoveryCmd(java.lang.String strCmd) throws android.os.RemoteException;
public java.lang.String registerDivX() throws android.os.RemoteException;
public boolean isDeviceActivated() throws android.os.RemoteException;
public java.lang.String deregisterDivX() throws android.os.RemoteException;
public void enableSpectrumData() throws android.os.RemoteException;
public void disableSpectrumData() throws android.os.RemoteException;
public android.app.tv.SpectrumDataInfo getSpectrumInfo() throws android.os.RemoteException;
public void enableGetMediaTotalTime() throws android.os.RemoteException;
public void disableGetMediaTotalTime() throws android.os.RemoteException;
public long getMediaTotalTime(java.lang.String url) throws android.os.RemoteException;
}

/* //device/java/android/android/app/ITvManager.aidl
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package android.app;

import android.app.PendingIntent;
import android.app.tv.ChannelFilter;
import android.app.tv.ChannelInfo;
import android.app.tv.SpectrumDataInfo;
//import android.app.*;
/**
 * System private API for talking with the tv manager service.
 *
 * {@hide}
 */
interface ITvManager {

        // luke.lin: NOT change the sequence for this field. Add for query the transaction code for native binder.
        // The function is android.os.IBinder.FIRST_CALL_TRANSACTION
        int queryTransactionCode(String functionName);

	//SystemCommandExecutor
	void stopRpcServer();
	void dumpPliMemoryUsage();
	void suspendRpcServer();
	void resumeRpcServer();
	void restoreSysDefSetting();
	void setScreenSaverTiming(int itiming);
	void setMenuLanguage(int iLanguage);
	void setSerialCode(String strSerCode);
	int getScreenSaverTiming();
	int getMenuLanguage();
	String getSerialCode();
	void startPerformanceLog();
	void stopPerformanceLog(String strFilePath);
	void clearEeprom();
	boolean readEepToFile();
	boolean writeFileToEep();
	void upgradeSystem();
	String getCurAPK();

	//TvChannelApiExecutor
    boolean tvAutoScanStart(int tvType, boolean bUpdateScan);
    void tvAutoScanStop();
    void tvAutoScanComplete(); 
    boolean tvSeekScanStart(boolean bSeekForward);
	void tvSeekScanStop();
    boolean tvScanManualStart(int iFreq, int iBandWidth, int iPhyChNum);
	void tvScanManualStop();
	void tvScanManualComplete();
	int tvScanInfo(int infoId);
	boolean isTvScanning();
	int getAtvSeqScanStartFreq();
	int getAtvSeqScanEndFreq();
	void saveChannel();
	void playNextChannel();
	void playPrevChannel();
	void playFirstChannel();
	void playHistoryChannel();
    void updateTvChannelList(in ChannelFilter filter);
    ChannelInfo getCurChannel();
    ChannelInfo getChannelInfoByIndex(int iIndex);
    int getChannelCount();
	boolean sortChannel(int policy);
	void playChannelByIndex(int iIndex);
	void playChannelByNum(int iNum);
	boolean playChannel(int iIndex);
	boolean playChannelByLCN(int iLcnNum);
	boolean playFirstChannelInFreq(int iFreq);
	boolean playChannelByChnumFreq(int iSysChNum, int iFreq, String tvSystem);
	boolean playNumberChannel(int majorNum, int minorNum, boolean isAudioFocus);
	void swapChannelByIdxEx(int iChIdx, boolean bSwapChNum, boolean bPlayAfterSwap);
	void swapChannelByNumEx(int iChNum, boolean bSwapChNum, boolean bPlayAfterSwap);
	void reloadLastPlayedSource();
	void setCurChannelSkipped(boolean bSkip);
	void setCurAtvSoundStd(int soundSystemId);
	void fineTuneCurFrequency(int iOffset, boolean bPerminant);
	void setCurChAudioCompensation(int iValue, boolean bApply);
	boolean setSource(int iIndex);
	void setBootSource(int sourceOpt);
	boolean setSourceAndDisplayWindow(int src, int x, int y, int width, int height);
	boolean getCurChannelSkipped();
	int getCurAtvSoundStd();
	int getCurChAudioCompensation();
	String getSourceList();
	int	getSourceListCnt(boolean bWithoutPlayback);
	long getCurSourceType();
	int getBootSource();
	int getCurTvType();
	int getCurLiveSource();

	//TvDisplaySetupApiExecutor
	void setBrightness(int iValue);
	void setContrast(int iValue);
	void setSaturation(int iValue);
	void setHue(int iValue);
	void setSharpness(boolean iApply, int iValue);
	void setColorTempMode(int level);
	void setDNR(int mode);
	void setAspectRatio(int ratio);
	void saveAspectRatio(int ratio);
	void setPictureMode(int mode);
	void setCurAtvColorStd(int colorStd);
	void setAvColorStd(int colorStd);
	boolean setVgaAutoAdjust();
	boolean setVgaHPosition(char ucPosition);
	boolean setVgaVPosition(char ucPosition);
	boolean setVgaPhase(char ucValue);
	boolean setVgaClock(char ucValue);
	void setMagicPicture(int magicPic);
	void setDCR(int iDCR);
	void setDCC(boolean iDccOn, boolean iIsApply);
	void setBacklight(int iValue);
	void set3dMode(int imode);
	void set3dDeep(int imode);
	void set3dLRSwap(boolean bOn);
	boolean set3dCvrt2D(boolean bOn, int iFrameFlag);
	void set3dStrength(int iStrength);
	boolean set3dModeAndChangeRatio(int iMode, boolean bMute, int iType);
	boolean getIs3d();
	void setColorTempRGain(int iValue);
	void setColorTempGGain(int iValue);
	void setColorTempBGain(int iValue);
	void setColorTempROffset(int iValue);
	void setColorTempGOffset(int iValue);
	void setColorTempBOffset(int iValue);
	void setDisplayWindow(int iX, int iY, int iWidth, int iHeight);
	void setDisplayWindowPositionChange(int iX, int iY, int iWidth, int iHeight);
	void setPanelOn(boolean bOn);
	void scaler_ForceBg(boolean bOn);
	void scaler_4k2kOSD_ForceBg(boolean bOn);
	void setOverScan(int h_start, int h_width, int v_start, int v_length);
	int getBrightness();
	int getContrast();
	int getSaturation();
	int getHue();
	int getSharpness();
	int getColorTempLevel();
	int getDNR();
	boolean getDCC();
	int getPictureMode();
	int getCurAtvColorStd();
	int getCurAtvColorStdNoAuto();
	int getAvColorStd();
	int getAvColorStdNoAuto();
	char getVgaHPosition();
	char getVgaVPosition();
	char getVgaPhase();
	char getVgaClock();

	int getAspectRatio(int iSourceOption);
	int getMagicPicture();
	int getDCR();
	String getDCRDemoDate();
	int getBacklight();
	int get3dMode();
	int get3dDeep();
	boolean get3dLRSwap();
	boolean get3dCvrt2D();
	boolean getPanelOn();
	String getResolutionInfo();
	boolean checkDviMode();
	String getOverScan();

	//TvFactoryApiExecutor
	void setScalerAutoColorRGain(int iValue);
	void setScalerAutoColorGGain(int iValue);
	void setScalerAutoColorBGain(int iValue);
	void setScalerAutoColorROffset(int iValue);
	void setScalerAutoColorGOffset(int iValue);
	void setScalerAutoColorBOffset(int iValue);

	//TvSoundSetupApiExecutor
	void setVolume(int iValue);
    void setMute(boolean mute);
    void setAndroidMode(int mode);
	int getAndroidMode();
	void setBalance(int iValue);
	void setBass(int iValue);
	void setTreble(int iValue);
    void setTrueSurround(boolean bEnable);
    void setClarity(boolean bEnable);
    void setTrueBass(boolean bEnable);
    void setSubWoof(boolean bEnable);
	void setSubWoofVolume(int iValue);
	void setEqualizerMode(int iMode);
	void setEqualizer(int iFreq, int iValue);
	void setOnOffMusic(boolean bOn);
	void setAudioHdmiOutput(int mode);
	void setSurroundSound(int mode);
	void setAudioSpdifOutput(int mode);
	void setWallEffect(boolean bEnable);
	void setAudioMode(int mode);
	void setAudioEffect(int audioEffect, int param);
	void setKeyToneVolume(int iVol);
	void setAudioChannelSwap(int sel);
	boolean setAutoVolume(boolean bEnable);
	boolean getAutoVolume();
	int getVolume();
	boolean getMute();
	int getBalance();
	int getBass();
	int getTreble();
	int getEqualizerMode();
	int getEqualizer(int iFreq);
	boolean getTrueSurround();
	boolean getClarity();
	boolean getTrueBass();
	boolean getSubWoof();
	int getSubWoofVolume();
	boolean getOnOffMusic();
	boolean getWallEffect();
	int getAudioMode();
 	boolean getAudioVolume();
	int getKeyToneVolume();
	int getAudioChannelSwap();

	//Invoke WLanSetupApiExecutor functions
	void setWLanTmpProfileName(String pStrName);
	void setWLanTmpProfileSSID(String pStrSSID);
	void setWLanTmpProfileWifiMode(int mode);
	void setWLanTmpProfileWifiSecurity(int security, String pStrKey);
	void setWLanTmpProfileDhcpHostIp(char ip1, char ip2, char ip3, char ip4);
	void setWLanTmpProfileDhcpStartIp(char ip1, char ip2, char ip3, char ip4);
	void setWLanTmpProfileDhcpEndIp(char ip1, char ip2, char ip3, char ip4);
	void setWLanTmpProfileWepIndex(int iIndex);
	void setWLanProfileCopyToTmp(int iProfileIndex);
	void setWLanProfileCopyFromTmp(int iProfileIndex);
	void setWLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4);		
	void setWLanProfileActiveIndex(int iProfileIndex);
	String getWLanProfileName(int iProfileIndex);
	String getWLanProfileSSID(int iProfileIndex);
	int getWLanProfileWifiMode(int iProfileIndex);
	int getWLanProfileWifiSecurity(int iProfileIndex);
	String getWLanProfileDhcpHostIp(int iProfileIndex);
	String getWLanProfileDhcpStartIp(int iProfileIndex);
	String getWLanProfileDhcpEndIp(int iProfileIndex);
	int getWLanProfileWepIndex(int iProfileIndex);
	int getWLanProfileActiveIndex();
	int getWLanProfileTotalNumber();
	String getWLanIpAddr(int netType);
	boolean getWLanDHCPState();
	int getWLanApListSize();
	String getWLanApName(int iApIndex);
	int getWLanApSecurity(int iApIndex);
	int getWLanApStrength(int iApIndex);
	void setWLanWpsMode(int mode);
	int getWLanWpsMode();
	String getWLanPinCode();
	void wLanConnectStart(int iProfileIndex);
	void wLanConnectStop(boolean bForce);
	int wLanConnectQueryState();
	int wLan0ActivateState();

	//LanSetupApiExecutor
	boolean wiredLanDHCPStart();
	void wiredLanDhcpStop(boolean bForceStop);
	int wiredLanDhcpQueryState();
	boolean getWiredLanDhcpEnable();
	String getWiredLanIpAddr(int netType, boolean bFromDatabase);
	String getMacAddressInfo(int iNetInterface);
	String getMacAddress();
	//tc_tan
	int getColorTempData(int index);
	void setColorTempData(int index, int iValue);
	boolean setScalerAutoColor();
	void resetColorTemp();
	
	void setWiredLanManualInit();
	void setWiredLanManualIp();
	void setWiredLanIpAddr(int netType, char ip1, char ip2, char ip3, char ip4);
	void setMacAddress(char mac1, char mac2, char mac3, char mac4, char mac5, char mac6);

	//ImageDecoderApiExecutor
	long startDecodeImage(boolean bBackupHttpFileSource);
	void decodeImage(String pFilePath, int transitionType);
	void decodeImageEx(String pFilePath, int transitionType, boolean bUpnpFile);
	long getDecodeImageResult();	
	void stopDecodeImage();
	void zoomIn();
	void zoomOut();
	void leftRotate();
	void rightRotate();
	void upRotate();
	void downRotate();
	void onZoomMoveUp();
	void onZoomMoveDown();
	void onZoomMoveLeft();
	void onZoomMoveRight();


	

	void enableQFHD(); //Request by FengWen that control Qual-FHD enable/disable
	void disableQFHD();
	void setSuperResolutionMode(boolean enable);
	int  getCurQuadFHDMode();
	boolean setCurQuadFHDMode(int mode);
	void setFunctionParser(int paramcounter, String param);
	String getFunctionParser(int paramcounter, String param);

        void setOverScanAndAdjustment(int h_ratio, int v_ratio, int h_start, int h_width, int v_start, int v_length, boolean applyalltiming, int customer);
        String getOverScanAndAdjustment(int customer);

	//NoSignal DisplayReady
	boolean getNoSignalDisplayReady();

	void setEnableBroadcast(boolean enable);	
	//Set interesting area of video
	void setVideoAreaOn(int x, int y, int w, int h, int plane);
	void setVideoAreaOff(int plane);

	//Test class get set
	//IcData getSetClass(IcData classData);	

	//factory menu api
	String getSystemVersion();
	String getBootcodeVersion();
	String getEepVersion();
	String getCpuVersion();
	String getReleaseDate();
	String getPanelName();

	int getUartMode();
	void setUartMode(int iValue);

	int getEepPage();
	int getEepOffset();
	int getEepData();
	void setEepPage(int iValue);
	void setEepOffset(int iValue);
	void setEepData(int iValue);

	void startWifi();
	void stopWifi();
	boolean getWifiState();

	int getNsta(int type);
	void setNsta(int type,int iValue);

	int getPattern();
	void setPattern(int iValue);
	void rebootSystem();
	void setFacSingleKey(boolean mode);
	boolean getFacSingleKey();
	void setWarmMode(boolean mode);
	void exitSkyworthFactorySet();
	void setBusoffMode(boolean mode);
	boolean getFacAutoScanGuide();
	void setFacAutoScanGuide(boolean mode);
	boolean getFacWarmMode();
	void setFacWarmMode(boolean mode);
	boolean getDDREnable();
	void setDDREnable(boolean mode);
	boolean getDDRPhaseShift();
	void setDDRPhaseShift(boolean mode);
	int getDDRStep();
	void setDDRStep(int iValue);
	int getDDRPeriod();
	void setDDRPeriod(int iValue);
	int getDDROffset();
	void setDDROffset(int iValue);
	boolean getLVDSEnable();
	void setLVDSEnable(boolean mode);
	int getLVDSDclkRange();
	void setLVDSDclkRange(int iValue);
	int getLVDSDclkFMDIV();
	void setLVDSDclkFMDIV(int iValue);
	boolean getLVDSNewMode();
	void setLVDSNewMode(boolean mode);
	int getLVDSPLLOffset();
	void setLVDSPLLOffset(int iValue);
	boolean getLVDSOnlyEvenOdd();
	void setLVDSOnlyEvenOdd(boolean mode);
	boolean getLVDSEvenOrOdd();
	void setLVDSEvenOrOdd(boolean mode);
	int getLVDSDrivingCurrent();
	void setLVDSDrivingCurrent(int iValue);
	String getBARCODE1();
	String getBARCODE2();
	String getBARCODE3();
	String getBARCODE4();
	void setBARCODE1(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8);
	void setBARCODE2(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8);
	void setBARCODE3(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8);
	void setBARCODE4(char bar1, char bar2, char bar3, char bar4, char bar5, char bar6, char bar7, char bar8);

	boolean gldcOsdShow(int mode);
	boolean isKeyDown(int key);
	void setInitialFlag(boolean bInitial);
	boolean getInitialFlag();
	int getDtvTime();
    boolean setRTKIRMouse(boolean setting);
	String getChannelNameList(int iStartIdx, int iContentLen, boolean bFilter);
	String getCurrentProgramInfo();
	String getCurrentProgramDescription();
	String getCurrentProgramRating();
	boolean hasCurrentProgramWithSubtitle();
	String getCurAtvSoundSelect();
	String getCurrentAudioLang();	
	String getCurInputInfo();
	String getCurrentSetting_tv(String tvStr);

	int getChannelFreqCount();
	int getChannelFreqByTableIndex(int index);
	String getChannelchannelNumByTableIndex(int index);
	int getChannelCountByFreq(int freq);
	int getCurChannelIndex();
	int getChannelListChannelCount();
 	String getChannelDataList(int iStartIdx, int iContentLen);
	boolean recoverVideoSize();
	String getVideoSize();
	void setVideoSize(int iX, int iY, int iWidth, int iHeight);
	String getCurDtvSoundSelectList();
	int getCurDtvSoundSelectCount();
	String getCurAtvSoundSelectList();
	int getCurAtvSoundSelectCount();
	boolean setDisplayFreeze(boolean enable);
	void setCaptionMode(int mode);
	void setAnalogCaption(int type);
	void setDigitalCaption(int type);
	void setChannelLockEnable(boolean enable);
	void setChannelFav(int index, boolean enable);
	void setChannelSkip(int index, boolean enable);
	void setChannelBlock(int index, boolean enable);
	void setChannelDel(int index, boolean enable);
	boolean getChannelFav(int index);
	boolean getChannelSkip(int index);
	boolean getChannelBlock(int index);
	boolean queryTvStatus(int type);
	void setHdmiAudioSource(int audioSource);
	boolean getIsContentLocked();	
	void setSourceLockEnable(boolean enable);
	boolean getSourceLockStatus(int source);
	void setSourceLockStatus(int source, boolean lock);
	boolean getSourceLockStatusByIndex(int srcIndex);
	void setSourceLockStatusByIndex(int srcIndex, boolean lock);
	int startRecordTs(String filePath, boolean bWithPreview);
	boolean stopRecordTs();
	void getEpgData(int iDayOffset, int iDayCount);
	void getEpgDataByLCN(int u16Lcn, int iDayOffset, int iDayCount);
	int getEpgListEpgCount();
	void setTvTimeZone(float timezone);
	String getEpgDataList(int iStartIdx, int iContentLen);
	
	void setParentalLockEnable(boolean enable);
	boolean getParentalLockEnable();
	void setParentalLockPasswd(int passwd);
	int getParentalLockPasswd();
	void setParentalLockRegion(int region);
	int getParentalLockRegion();
	void setParentalRatingDvb(int rating);
	int getParentalRatingDvb();
	void setPasswordReverify(boolean isVerified);
	boolean getPasswordReverify();
	void setPasswordVerified(int lockType);
	boolean getPasswordVerified(int lockType);
	void setPipEnable(boolean enable, boolean bKeepSubSourceAlive);
	boolean getPipEnable();	
	void setPipSubSource(int subSource, boolean apply);
	int getPipSource(int vout);
	void clearPipSourceIndicator(int source);
	void setPipSubMode(int subMode);
	int getPipSubMode();
	void setPipSubPosition(int subPos);
	int getPipSubPosition();
	void setPipSubDisplayWindow(int x, int y, int width, int height);

	void transcodeControlStart();
	void transcodeControlStop();
	void transcodeControlPause();
	void transcodeControlResume();
	void transcodeControlStartHttp();
	void transcodeControlStopHttp();
	
	String getRecoveryCmd();
	void setRecoveryCmd(String strCmd);

	String registerDivX();
	boolean isDeviceActivated();
	String deregisterDivX();

	void enableSpectrumData();
	void disableSpectrumData();
	SpectrumDataInfo getSpectrumInfo();
	void enableGetMediaTotalTime();
    void disableGetMediaTotalTime();
    long getMediaTotalTime(String url);
}



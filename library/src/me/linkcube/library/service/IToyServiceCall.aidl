package me.linkcube.library.service;

import java.util.List;
import java.util.Map;

interface IToyServiceCall {
	
	boolean connectToy(String deviceName, String macAddress);
	
	boolean disconnectToy(String deviceName, String macAddress);
	
	boolean closeToy();

	int cacheToySpeed(int speed,boolean overtime);
	
	int cacheSexPositionMode(int mode);
	
	int cacheShake(long shakeSpeed,boolean overtime);
	
	void setShakeSensitivity(int level);
	
	void setVoiceSensitivity(int level);

	int setWave(long waveng);
	
	boolean setWaveMode(int index, int val);
	
	boolean setShakeMode(int index, int val);	
	
	boolean checkConnection();
}


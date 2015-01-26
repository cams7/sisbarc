For Linux QBEX: 
	cd ~/desenv/git/github/sisbarc/mobile/acende_apaga_leds-android

For Windows VAIO:
	cd ~/desenv/github/sisbarc/mobile/acende_apaga_leds-android

mvn clean install

mvn android:emulator-start
mvn android:deploy
mvn android:undeploy

android avd
emulator -avd API_10



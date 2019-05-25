import obd
import time
import paho.mqtt.client as client
import json
connection = obd.Async("\\\\.\\COM14",fast=False)
broker = "192.168.0.31"
client = client.Client("Client-0001")
client.connect(broker)
class ObdDataHandler:
    def __init__(self):
        self.speed = 0
        self.rpm = 0
        self.eload = 0
        self.tpos = 0
        self.ctemp = 0
        self.maf = 0
        
    def writeFile(self):
        d=open("data.csv",'a+')
        d.write(str(self.speed)+", "+str(self.rpm)+", "+str(self.tpos)+"\n")
        d.close()
        self.publishMsg()
        
    def getRPM(self,r):
        if not r.is_null():
            self.rpm = int(r.value.magnitude)
    def getEload(self,e):
        if not e.is_null():
            self.eload = int(e.value.magnitude)
    def getSpeed(self,s):
        if not s.is_null():
            self.speed = int(s.value.magnitude)
    def getTpos(self,t):
        if not t.is_null():
            self.tpos = int(t.value.magnitude)
    def getCtemp(self,ct):
        if not ct.is_null():
            self.ctemp = int(ct.value.magnitude)
    def getMaf(self,m):
        if not m.is_null():
            self.maf = int(m.value.magnitude)

    def publishMsg(self):
        data = {
            "RPM":self.rpm,
            "ELOAD":self.eload,
            "SPEED":self.speed,
            "TPOS":self.tpos,
            "CTEMP":self.ctemp,
            "MAF":self.maf
            }
        pubdata = json.dumps(data)
        #data = '{\"RPM\":{},\"ELOAD\":{},\"SPEED\":{},\"TPOS\":{},\"CTEMP\":{},\"MAF\":{}}'.format(self.rpm,self.eload,self.speed,self.tpos,self.ctemp,self.maf)
        print(data)
        client.publish("obd",pubdata)

obj=ObdDataHandler()
connection.watch(obd.commands.SPEED, callback=obj.getSpeed)
connection.watch(obd.commands.RPM, callback=obj.getRPM)
#connection.watch(obd.commands.ENGINE_LOAD, callback=obj.getEload)
connection.watch(obd.commands.THROTTLE_POS, callback=obj.getTpos)
connection.watch(obd.commands.MAF, callback=obj.getMaf)
connection.watch(obd.commands.COOLANT_TEMP, callback=obj.getCtemp)
connection.start()

while True:
    time.sleep(1)
    obj.writeFile()

connection.stop()
connection.close()

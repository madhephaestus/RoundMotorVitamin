import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.parametrics.*;
import eu.mihosoft.vrl.v3d.Transform;

CSG getNut(){
	String type= "roundMotor"
	if(args==null)
		args=["WPI-gb37y3530-50en"]
	StringParameter size = new StringParameter(	type+" Default",
										args.get(0),
										Vitamins.listVitaminSizes(type))
	//println "Database loaded "+database
	HashMap<String,Object> config = Vitamins.getConfiguration( type,size.getStrValue())
	// Measurments from http://image.dfrobot.com/image/data/FIT0185/FIT0185_Dimension.PNG
	LengthParameter printerOffset = new LengthParameter("printerOffset",0.25,[2,0.001])
	double boltHoleDiameter = config.boltHolePatternDiameter
	double shaftCenterOffset = config.shaftCenterOffset
	double shaftCollarDiameter = (config.shaftCollarDiameter/2.0)+printerOffset.getMM()
	double shaftCollarHeight = config.shaftCollarHeight 
	double motorBodyRadius = (config.motorBodyRadius/2.0)+printerOffset.getMM()
	double bodyLength=config.bodyLength
	HashMap<String,Object> configShaft = Vitamins.getConfiguration( "dShaft",config.dshaft)
	double shaftDiameter = configShaft.shaftDiameter
	double totalShaftLength = config.totalShaftLength
	double dShaftSection =configShaft.length
	double dShaftStart = totalShaftLength-dShaftSection
	double shaftRadius = (shaftDiameter/2)+printerOffset.getMM()
	LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[180,10])
	boltLength.setMM(dShaftStart)
	CSG body =new Cylinder(motorBodyRadius,motorBodyRadius,bodyLength,(int)30).toCSG() // a one line Cylinder
				.toZMax()
	CSG collar =new Cylinder(shaftCollarDiameter,shaftCollarDiameter,shaftCollarHeight,(int)20).toCSG() // a one line Cylinder
	CSG shaft =new Cylinder(shaftRadius,shaftRadius,dShaftStart,(int)20).toCSG() // a one line Cylinder
	
	HashMap<String,Object> boltConfig = Vitamins.getConfiguration( config.boltType,config.boltSize)
	def headDiameter=Double.parseDouble(boltConfig.get("headDiameter").toString())+printerOffset.getMM()
	def headHeight=Double.parseDouble(boltConfig.get("headHeight").toString())
	def keyDepth=Double.parseDouble(boltConfig.get("keyDepth").toString())
	def keySize=Double.parseDouble(boltConfig.get("keySize").toString())
	def outerDiameter=Double.parseDouble(boltConfig.get("outerDiameter").toString())+printerOffset.getMM()
	if(boltConfig.get("boltLength")!=null)
		boltLength.setMM(boltConfig.get("boltLength"))

	//println boltConfig
	//println boltLength.getMM()
	CSG head =new Cylinder(headDiameter/2,headDiameter/2,headHeight,20).toCSG() // a one line Cylinder
				.toZMin()
	CSG boltshaft =new Cylinder(outerDiameter/2,outerDiameter/2,boltLength.getMM(),20).toCSG() // a one line Cylinder
				.toZMax()	
	CSG bolt = head.union(boltshaft)
				.toZMax()
				.movez(dShaftStart)
				//.rotx(180)
				.movey(boltHoleDiameter/2)

	pshaft=CSG.unionAll([collar,shaft])
						.movex(shaftCenterOffset)
	CSG bolts = bolt
	for(int i=config.boltHolePatternAngleOffset;i<360;i+=config.boltHolePatternAngleIncrement){
		bolts=bolts.union(bolt.rotz(i))
	}
	CSG wholeMotor = CSG.unionAll([pshaft,bolts,body])
					.movex(-shaftCenterOffset)
					.rotz(-90)
	wholeMotor.addSlicePlane(new Transform())
	wholeMotor.addSlicePlane(new Transform()
						.movez(-1)
	)
	wholeMotor.addSlicePlane(new Transform()
						.movez(shaftCollarHeight+0.1)
	)
	//return bolt
	return wholeMotor
		.setParameter(size)
		.setRegenerate({getNut()})
}
return getNut()

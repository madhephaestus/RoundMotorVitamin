import eu.mihosoft.vrl.v3d.parametrics.*;
CSG getNut(){
	String type= "dShaft"
	if(args==null)
		args=["WPI-gb37y3530-50en"]
	StringParameter size = new StringParameter(	type+" Default",
										args.get(0),
										Vitamins.listVitaminSizes(type))
	//println "Database loaded "+database
	HashMap<String,Object> config = Vitamins.getConfiguration( type,size.getStrValue())
	LengthParameter printerOffset = new LengthParameter("printerOffset",0.25,[2,0.001])


	double finalp = config.shaftDiameter/2+(printerOffset.getMM()/2)

	double finalf = config.shaftDSectionDiameter+(printerOffset.getMM()/2)

	CSG pshaft =new Cylinder(finalp,finalp,config.length,(int)30).toCSG() // a one line Cylinder
	CSG flat = new Cube(	finalp*2,// X dimention
				finalf,// Y dimention
				config.length//  Z dimention
				).toCSG()
				.toYMin()
				.toZMin()
				.movey(pshaft.getMinY())

	return pshaft.intersect(flat)
		.setParameter(size)
		.setRegenerate({getNut()})
}
return getNut()

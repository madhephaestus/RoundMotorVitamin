

 import eu.mihosoft.vrl.v3d.parametrics.*;
CSG getNut(){
	String type= "dShaft"
	if(args==null)
		args=["WPI-gb37y3530-50en"]
	StringParameter size = new StringParameter(	type+" Default",
										args.get(0),
										Vitamins.listVitaminSizes(type))
	//println "Database loaded "+database
	HashMap<String,Object> servoConfig = Vitamins.getConfiguration( type,size.getStrValue())
	if(args == null)
		args = [6.0,5.4,11]

	LengthParameter printerOffset = new LengthParameter("printerOffset",0.25,[2,0.001])


	double finalp = args.get(0)/2+(printerOffset.getMM()/2)

	double finalf = args.get(1)+(printerOffset.getMM()/2)

	CSG pshaft =new Cylinder(finalp,finalp,args.get(2),(int)30).toCSG() // a one line Cylinder
	CSG flat = new Cube(	finalp*2,// X dimention
				finalf,// Y dimention
				args.get(2)//  Z dimention
				).toCSG()
				.toYMin()
				.toZMin()
				.movey(pshaft.getMinY())

	return pshaft.intersect(flat)
		.setParameter(size)
		.setRegenerate({getNut()})
}
return getNut()

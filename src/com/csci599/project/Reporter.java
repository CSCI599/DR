package com.csci599.project;

public class Reporter {
	public static void reporter() {
		try {
			String className = "net.sourceforge.cobertura.coveragedata.ProjectData";
			String methodName = "saveGlobalProjectData";
			Class saveClass = Class.forName(className);
			java.lang.reflect.Method saveMethod = saveClass.getDeclaredMethod(
					methodName, new Class[0]);
			saveMethod.invoke(null, new Object[0]);
			System.out.println("Dumped Data");
		} catch (Exception t) {
			System.out.println(t.toString());
		}
	}
}

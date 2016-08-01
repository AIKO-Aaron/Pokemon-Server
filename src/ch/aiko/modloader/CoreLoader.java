package ch.aiko.modloader;

public abstract class CoreLoader {

	@GameInit
	public abstract void init() throws Throwable;
	
	@GameInit(type = InitMethod.PRE_INIT)
	public abstract void preInit() throws Throwable;
	
	@GameInit(type = InitMethod.POST_INIT)
	public abstract void postInit() throws Throwable;
	
}

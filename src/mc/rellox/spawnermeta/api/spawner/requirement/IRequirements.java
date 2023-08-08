package mc.rellox.spawnermeta.api.spawner.requirement;

public interface IRequirements {
	
	public static IRequirements empty = new IRequirements() {
		@Override
		public ILight light() {
			return ILight.empty;
		}
		@Override
		public IMaterial ground() {
			return IMaterial.empty;
		}
		@Override
		public IMaterial environment() {
			return IMaterial.empty;
		}
	};

	ILight light();
	
	IMaterial ground();
	
	IMaterial environment();
	
	static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		public ILight light;
		public IMaterial ground;
		public IMaterial environment;
		
		
		public IRequirements build() {
			if(light == null) light = ILight.empty;
			if(ground == null) ground = IMaterial.empty;
			if(environment == null) environment = IMaterial.empty;
			return new IRequirements() {
				@Override
				public ILight light() {
					return light;
				}
				@Override
				public IMaterial ground() {
					return ground;
				}
				@Override
				public IMaterial environment() {
					return environment;
				}
			};
		}
		
	}

}

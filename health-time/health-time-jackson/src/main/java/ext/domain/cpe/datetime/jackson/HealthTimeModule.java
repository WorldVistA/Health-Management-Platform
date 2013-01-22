package EXT.DOMAIN.cpe.datetime.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import EXT.DOMAIN.cpe.datetime.IntervalOfTime;
import EXT.DOMAIN.cpe.datetime.PointInTime;

public class HealthTimeModule extends SimpleModule {
    public HealthTimeModule() {
        super("HealthTimeModule", ModuleVersion.instance.version());

        addDeserializer(PointInTime.class, new PointInTimeDeserializer());
        addDeserializer(IntervalOfTime.class, new IntervalOfTimeDeserializer());

        addSerializer(PointInTime.class, new PointInTimeSerializer());
        addSerializer(IntervalOfTime.class, new IntervalOfTimeSerializer());
    }
}

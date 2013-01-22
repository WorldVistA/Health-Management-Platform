package EXT.DOMAIN.cpe.vpr.pom;

public class DefaultNamingStrategy implements INamingStrategy {
    @Override
    public String collectionName(Class entityClass) {
        return entityClass.getSimpleName().toLowerCase(); // maybe try camelCase -> hyphenated-lower-case
    }
}

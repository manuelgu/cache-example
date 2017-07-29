package eu.manuelgu.caching;

public class Caching {

    public static void main(String[] args) throws Throwable {
        CacheLoader cacheLoader = new CacheLoader();

        // Start
        long start = System.currentTimeMillis();

        // Executes
        System.out.println(cacheLoader.getScore(1));

        // Stop timer
        System.out.println( (System.currentTimeMillis() - start) + " ms to fetch");
    }
}

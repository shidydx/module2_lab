package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher fetcher;          // underlying fetcher (e.g., DogApiBreedFetcher)
    private final Map<String, List<String>> cache; // stores cached results
    private int callsMade = 0;                   // how many times we've called the underlying fetcher

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        // Normalize the key (optional, but avoids case mismatch issues)
        String key = breed.toLowerCase();

        // Return cached result if available
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        try {
            // Call the underlying fetcher
            List<String> subBreeds = fetcher.getSubBreeds(breed);
            callsMade++;

            // Cache the successful result
            cache.put(key, subBreeds);
            return subBreeds;

        } catch (BreedNotFoundException e) {
            // Do NOT cache failed results
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}

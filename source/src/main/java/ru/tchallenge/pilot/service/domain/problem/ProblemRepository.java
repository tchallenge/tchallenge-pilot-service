package ru.tchallenge.pilot.service.domain.problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;

import ru.tchallenge.pilot.service.context.ManagedComponent;
import ru.tchallenge.pilot.service.utility.data.GenericRepository;
import ru.tchallenge.pilot.service.utility.data.Id;

@ManagedComponent
public class ProblemRepository extends GenericRepository {

    private static final int MAX_RANDOM_ITERATIONS = 3;

    public List<ProblemDocument> findAll() {
        return documents()
                .find()
                .map(ProblemDocument::new)
                .into(new ArrayList<>());
    }

    public List<ProblemDocument> findByIds(final List<Id> ids) {
        return documents()
                .find()
                .filter(ProblemDocument.filterByIds(ids))
                .map(ProblemDocument::new)
                .into(new ArrayList<>());
    }

    public List<ProblemDocument> findRandom(final ProblemRandomInvoice invoice) {
        int iterations = 0;
        final List<Bson> pipeline = randomAggregationPipeline(invoice);
        final Map<Id, ProblemDocument> randomProblems = new HashMap<>();
        while (randomProblems.size() < invoice.getNumber() && iterations < MAX_RANDOM_ITERATIONS) {
            randomProblems.putAll(aggregate(pipeline));
            iterations++;
        }
        return new ArrayList<>(randomProblems.values());
    }

    private List<Bson> randomAggregationPipeline(final ProblemRandomInvoice invoice) {
        final Bson filter = ProblemDocument.randomFilter(invoice.getCategories(), invoice.getDifficulty());
        final Bson match = Aggregates.match(filter);
        final Bson sample = Aggregates.sample(invoice.getNumber());
        return Lists.newArrayList(match, sample);
    }

    private Map<Id, ProblemDocument> aggregate(final List<Bson> pipeline) {
        return documents()
                .aggregate(pipeline)
                .map(ProblemDocument::new)
                .into(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(ProblemDocument::getId, p -> p));
    }

    @Override
    protected String getCollectionName() {
        return "problems";
    }
}

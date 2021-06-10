package com.github.community.config;

import com.duby.util.TrieFilter.TrieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrieFilterConfig {

    @Bean
    public TrieFilter trieFilter() {
        TrieFilter trieFilter = new TrieFilter();
        trieFilter.batchAdd("sensi_words.txt");
        return trieFilter;
    }
}

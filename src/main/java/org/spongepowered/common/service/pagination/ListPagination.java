/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.service.pagination;

import static org.spongepowered.common.util.SpongeCommonTranslationHelper.t;

import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pagination working with a list of values.
 */
class ListPagination extends ActivePagination {
    private final List<List<Text>> pages;

    public ListPagination(CommandSource src, PaginationCalculator<CommandSource> calc, List<Map.Entry<Text, Integer>> lines,
            Text title, Text header, Text footer, String padding) {
        super(src, calc, title, header, footer, padding);
        List<List<Text>> pages = new ArrayList<List<Text>>();
        List<Text> currentPage = new ArrayList<Text>();
        int currentPageLines = 0;

        for (Map.Entry<Text, Integer> ent : lines) {
            if (getMaxContentLinesPerPage() > 0 && ent.getValue() + currentPageLines > getMaxContentLinesPerPage() && currentPageLines != 0) {
                currentPageLines = 0;
                pages.add(currentPage);
                currentPage = new ArrayList<Text>();
            }
            currentPageLines += ent.getValue();
            currentPage.add(ent.getKey());
        }
        if (currentPageLines > 0) {
            pages.add(currentPage);
        }
        this.pages = pages;
    }

    @Override
    protected Iterable<Text> getLines(int page) throws CommandException {
        if (page < 1) {
            throw new CommandException(t("Page %s does not exist!", page));
        } else if (page > this.pages.size()) {
            throw new CommandException(t("Page %s is too high", page));
        }
        return this.pages.get(page - 1);
    }

    @Override
    protected boolean hasPrevious(int page) {
        return page > 1;
    }

    @Override
    protected boolean hasNext(int page) {
        return page < this.pages.size();
    }

    @Override
    protected int getTotalPages() {
        return this.pages.size();
    }
}

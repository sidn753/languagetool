#!/bin/sh

spell_uk_dir=$HOME"/work/ukr/spelling/spell-uk"
FLAGS="-f"

[ -s tagged.main.txt ] && mv -f tagged.main.txt tagged.main.txt.bak
[ -s tagged.main.dups.txt ] && mv -f tagged.main.dups.txt tagged.main.dups.txt.bak
[ -s tagged.main.uniq.txt ] && mv -f tagged.main.uniq.txt tagged.main.uniq.txt.bak

make -C $spell_uk_dir/src/Dictionary uk_words.tag && \
make -C $spell_uk_dir/src/Affix uk_affix.tag && \
$spell_uk_dir/bin/tag/mk_pos_dict.py && \
echo "Running regression diffs..." && \
sort tagged.main.txt | tee tagged.main.sorted.tmp | uniq -d > tagged.main.dups.txt && \
cat tagged.main.sorted.tmp | uniq -u > tagged.main.uniq.txt && \
(diff tagged.main.uniq.txt.bak tagged.main.uniq.txt > tu.diff || /bin/true) && \
(diff tagged.main.dups.txt.bak tagged.main.dups.txt > td.diff || /bin/true) && \
./make-dict-uk-mfl.sh $FLAGS && \
mv -f *.dict ukrainian_tags.txt ../
rm -f tagged.main.sorted.tmp

#
# Generates list of uniq tags used in the dictionary and (if old file found generates the difference)
#

mv -f all_tags.txt all_tags.txt.old
sed -r "s/:/\n/g" ../ukrainian_tags.txt | sed -r "s/\&//g" | sort | uniq > all_tags.txt
diff all_tags.txt.old all_tags.txt > all_tags.diff

echo "Tagset diff:"
cat all_tags.diff
echo "-------"

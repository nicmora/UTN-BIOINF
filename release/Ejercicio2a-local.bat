makeblastdb -dbtype prot -in swissprot
blastp -db swissprot -query input_ej2a.txt -out output_ej2a-local.blast
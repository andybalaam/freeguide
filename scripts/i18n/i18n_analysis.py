#!/usr/bin/python -u

# Run this script from within its own directory

import re, os, sys

if len( sys.argv ) > 1:
	restrict_to_langs = sys.argv[1:]
else:
	restrict_to_langs = None

MISSING_STR = "__MISSING__"

main_language = ""

ignore_missing_strings_languages = []
ignore_missing_strings_languages.append( "en_US" )

non_lang_strings = {}
non_lang_strings["all"] = []
non_lang_strings["all"].append( "favourite_name_equals_template" );
non_lang_strings["all"].append( "favourite_name_regexp_template" );
non_lang_strings["all"].append( "favourite_name_template" );
non_lang_strings["all"].append( "minus" );
non_lang_strings["all"].append( "plus" );

non_lang_strings["de"] = []
non_lang_strings["de"].append( "ok" );
non_lang_strings["de"].append( "r" );
non_lang_strings["de"].append( "ip" );
non_lang_strings["de"].append( "timedialog.ok" );

non_lang_strings["fr"] = []
non_lang_strings["fr"].append( "ok" );
non_lang_strings["fr"].append( "file" );
non_lang_strings["fr"].append( "ip" );
non_lang_strings["fr"].append( "timedialog.ok" );

main_translation_file_re = re.compile( r'MessagesBundle_?(.*?)\.properties' )

translation_res = []
translation_res.append(
	re.compile( r'getLocalizedMessage\(\s*\"(.*?)\"' ) )
translation_res.append(
	re.compile( r'getLocalizedString\(\s*\"(.*?)\"' ) )
	
exclude_path_res = []
exclude_path_res.append(
	re.compile( "/freeguide/plugins/program/freeguide/updater/" ) )

java_dir = "../../src/freeguide"
i18n_dir = "../../src/resources/i18n"

string_2_java_filename = {}
ignored_strings = []

en_GB_filenames = []
languages = []

i18n_dir_files = os.listdir( i18n_dir )
i18n_dir_files.sort()

# First search for strings in the Java code needing translation
count_java_files = 0
count_strings_to_translate = 0
for (dirpath, dirnames, filenames) in os.walk( java_dir ):
	for fn in filenames:
		if fn.endswith( ".java" ):
			count_java_files += 1
			exclude_this = False
			
			full_path =  dirpath + "/" + fn
			for r in exclude_path_res:
				if r.search( full_path ):
					exclude_this = True
					break
			
			fl = file( full_path, 'r' )
			fl_contents = fl.read()
			
			#print "y " + full_path
			for regexp in translation_res:
				matches = regexp.findall( fl_contents )
				
				for m in matches:
					#print "x " + m
					if exclude_this:
						ignored_strings.append( m )
					else:
						count_strings_to_translate += 1
						string_2_java_filename[m] = MISSING_STR + ":" + fn
				
			fl.close()

# Find out what languages we have translations for
langs_done = []
for fn in i18n_dir_files:
	m = main_translation_file_re.match( fn )
	if m:
		lang = m.group( 1 )
		if lang not in langs_done:
			langs_done.append( lang )
			languages.append( ( lang, [], {}, [], [], [], {}, {} ) )

# For each language
for ( lang,
	  filenames,
	  translations,
	  not_translated_filenames,
	  not_translated_strings,
	  just_en_translation_strings,
	  unused_strings, 
	  translated_twice_strings ) in languages:
	
	# Copy the string2javafilename map for this language
	map_copy = {}
	for key in string_2_java_filename.keys():
		translations[key] = ( string_2_java_filename[key], "" )
	
	# Search for strings translated into this language
	for fn in i18n_dir_files:
		if fn.endswith( "_%s.properties" % lang ) or contains_no_lang_part( fn ):
			filenames.append( fn )
			fl = file( i18n_dir + "/" + fn, 'r' )
			for ln in fl:
				if ln.find( '=' ) != -1:
					( key, val ) = ln.split( '=', 1 )
					key = key.strip()
					
					if translations.has_key( key ):
						if translations[key][0].startswith( MISSING_STR ):
							translations[key] = ( val.strip(), fn )
						else:
							if not translated_twice_strings.has_key( key ):
								translated_twice_strings[key] = []
							translated_twice_strings[key].append(
								translations[key][1] )
							translated_twice_strings[key].append( fn )
					elif not ( key.endswith( "_desc" )
						    or key.endswith( "_name" )
							or key in ignored_strings ):
						unused_strings[key] = fn
	
	# Make a list of strings not translated into this language
	for key in translations.keys():
		( value, fn ) = translations[key]
		if value.startswith( MISSING_STR ):
			not_translated_strings.append( ( key, value[len(MISSING_STR)+1:] ) )
			del translations[key]
	
	if lang == main_language:
		en_GB_filenames = filenames
		print "hhh", len( en_GB_filenames )
		en_GB_translations = translations

# Find which files were not translated
for ( lang,
	  filenames,
	  translations,
	  not_translated_filenames,
	  not_translated_strings,
	  just_en_translation_strings,
	  unused_strings, 
	  translated_twice_strings ) in languages:

	if lang != main_language:
		for fn in en_GB_filenames:
			modified_fn = fn.replace( ".%s." % main_language, ".%s." % lang )
			if modified_fn not in filenames:
				not_translated_filenames.append( fn )

# Find which strings are still in English
for ( lang,
	  filenames,
	  translations,
	  not_translated_filenames,
	  not_translated_strings,
	  just_en_translation_strings,
	  unused_strings, 
	  translated_twice_strings ) in languages:
	
	if lang != main_language:
		for key in translations.keys():
			if          key not in non_lang_strings["all"] \
					and ( not non_lang_strings.has_key( lang ) or
					      key not in non_lang_strings[lang] ):
				if en_GB_translations.has_key(key):
					if translations[key][0] == en_GB_translations[key][0]:
						just_en_translation_strings.append(
							( key, translations[key][1] ) )

# Print out our findings
print "%d *.java files evaluated." % count_java_files
print "%d strings requiring translation." % count_strings_to_translate
print "%d different translation languages found." % len( languages )
print

for ( lang,
	  filenames,
	  translations,
	  not_translated_filenames,
	  not_translated_strings,
	  just_en_translation_strings,
	  unused_strings, 
	  translated_twice_strings ) in languages:
	
	if not restrict_to_langs or lang in restrict_to_langs:
		
		title = "Report for language %s:" % lang
		print
		print title
		print "-" * len( title )
		
		if lang != main_language and \
				lang not in ignore_missing_strings_languages:
			if len( not_translated_filenames ) > 0:
				print "  The following i18n files were not copied from %s:" \
					% main_language
				for fn in not_translated_filenames:
					print "    %s" % fn
				print
			else:
				print "  All required files were copied from %s." % main_language
		
		if lang not in ignore_missing_strings_languages:
			if len( not_translated_strings ) > 0:
				print "  The following strings were not present:"
				for ( s, fn ) in not_translated_strings:
					print "    %40s : %s" % ( fn, s )
				print
			else:
				print "  All strings were present."
		
		if lang != main_language:
			if len( just_en_translation_strings ) > 0:
				print "  The following strings were identical to the %s version:" \
					% main_language
				for ( s, fn ) in just_en_translation_strings:
					print "    %40s : %s" % ( fn, s )
				print
			else:
				print "  No strings were identical to the %s version." \
					% main_language
		
		if len( unused_strings ) > 0:
			print "  The following strings were unused:"
			for key in unused_strings.keys():
				print "    %40s : %s" % ( key, unused_strings[key] )
			print
		else:
			print "  No strings were unused."
			
		if len( translated_twice_strings ) > 0:
			print "  The following strings were translated twice:"
			for key in translated_twice_strings.keys():
				print "    %40s : " % key,
				s = ""
				for fn in translated_twice_strings[key]:
					s += fn + ", "
				print s[:-2]
			print
		else:
			print "  No strings were translated twice."
		
		print


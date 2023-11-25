INSERT INTO public.season (id, season)
VALUES (1, 2023),
    (2, 2015);
INSERT INTO team (
        id,
        "key",
        name,
        nickname,
        long_name,
        alt_name_1,
        alt_name_2,
        color,
        alt_color,
        logo_url,
        espn_id,
        scrape_src_id,
        published_at
    )
VALUES (
        1,
        'georgetown',
        'Georgetown',
        'Hoyas',
        'Georgetown Hoyas',
        'Georgetown',
        'Hoyas',
        '002b5c',
        'ffffff',
        'https://a.espncdn.com/i/teamlogos/ncaa/500/153.png',
        153,
        1,
        now()
    ),
    (
        2,
        'villanova',
        'Villanova',
        'Wildcats',
        'Villanova Wildcats',
        'Villanova',
        'Wildcats',
        '003366',
        'ffffff',
        'https://a.espncdn.com/i/teamlogos/ncaa/500/222.png',
        222,
        1,
        now()
    ),
    (
        3,
        'syracuse',
        'Syracuse',
        'Orange',
        'Syracuse Orange',
        'Syracuse',
        'Orange',
        'f37321',
        'ffffff',
        'https://a.espncdn.com/i/teamlogos/ncaa/500/183.png',
        183,
        1,
        now()
    );
INSERT INTO conference (
        id,
        "key",
        name,
        alt_name,
        logo_url,
        espn_id,
        scrape_src_id,
        published_at
    )
VALUES (
        1,
        'big-east',
        'Big East',
        'Big East',
        'https://a.espncdn.com/i/teamlogos/ncaa/500/5.png',
        5,
        1,
        now()
    ),
    (
        2,
        'acc',
        'ACC',
        'ACC',
        'https://a.espncdn.com/i/teamlogos/ncaa/500/152.png',
        152,
        1,
        now()
    );